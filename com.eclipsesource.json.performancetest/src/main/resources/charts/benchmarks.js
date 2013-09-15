/*******************************************************************************
 * Copyright (c) 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
var benchmarks = {};

(function(){

  var mergeObjects = function() {
    var result = {};
    var addEntry = function( entry ) {
      result[ entry.key ] = entry.value;
    };
    for( var i = 0; i < arguments.length; i++ ) {
      d3.entries( arguments[i] ).forEach( addEntry );
    }
    return result;
  };

  var or0 = function( func ) {
    return function( values ) {
      return values.length === 0 ? 0 : func( values );
    };
  };
  var max0 = or0( d3.max );
  var min0 = or0( d3.min );
  var median0 = or0( d3.median );

  var selectBestUnit = function( measurements ) {
    // TODO ensure same units in all measurements
    var max = max0( d3.merge( measurements.map( function( m ) {
      return m.numbers;
    })));
    var units = measurements[0].units;
    var unitNames = d3.keys( units );
    var tickLengths = unitNames.map( function( name ) {
      var scale = d3.scale.linear().domain([0, max / units[name]]);
      return scale.ticks(5).map(scale.tickFormat(5)).join().length;
    });
    var min = tickLengths.indexOf( min0( tickLengths ) );
    return unitNames[min];
  };

  var unique = function( array ) {
    var buffer = {};
    return array.filter( function( d ) {
      var isNew = !( d in buffer );
      buffer[ d ] = true;
      return isNew;
    });
  };

  benchmarks.valuesForVariable = function( measurements, name ) {
    return unique( measurements.map( function( m ) {
      return m.variables[ name ];
    }));
  };

  benchmarks.filterMeasurements = function( measurements, name, value ) {
    return measurements.filter( function( m ) {
      return name === undefined || m.variables[ name ] === value;
    });
  };

  benchmarks.createChart = function( measurements, options ) {

    var opts = mergeObjects( {
      width: 800,
      height: -1,
      margin: { top: 20, right: 150, bottom: 30, left: 150 },
      color: d3.scale.category20b(),
      unit: selectBestUnit( measurements )
    }, options );

    var groups = benchmarks.valuesForVariable( measurements, opts.groups );
    var variants = benchmarks.valuesForVariable( measurements, opts.variants );

    var data = groups.map( function( group ) {
      var groupMeasurements = benchmarks.filterMeasurements( measurements, opts.groups, group );
      return {
        name: group,
        results: variants.map( function( variant ) {
          return {
            name: variant,
            measurements: benchmarks.filterMeasurements( groupMeasurements, opts.variants, variant )
          };
        })
      };
    });

    var calculateHeight = function( options ) {
      if( options.height !== -1 ) {
        return options.height;
      }
      var groupHeight = Math.max( variants.length * 10, 20 );
      var barsHeight = groups.length * groupHeight;
      var padding = ( groups.length + 1 ) * groupHeight * 0.1;
      return barsHeight + padding + options.margin.top + options.margin.bottom;
    };

    var numbers = function( measurements ) {
      return d3.merge( measurements.map( function( m ) {
        return m.numbers.map( function( number ) {
          return number / m.units[ opts.unit ];
        });
      }));
    };

    var margin = opts.margin;
    var width = opts.width - margin.left - margin.right;
    var height = calculateHeight( opts ) - margin.top - margin.bottom;

    var x = d3.scale.linear().range( [0, width] );
    var y = d3.scale.ordinal().rangeRoundBands( [0, height], 0.1 );
    var y1 = d3.scale.ordinal();
    var color = opts.color;
    var xAxis = d3.svg.axis().scale( x ).orient( "bottom" ).tickSize( -height );
    var yAxis = d3.svg.axis().scale( y ).orient( "left" );

    var svg = d3.select( "body" ).append( "svg" )
        .attr( "width", width + margin.left + margin.right )
        .attr( "height", height + margin.top + margin.bottom )
      .append( "g" )
        .attr( "transform", "translate(" + margin.left + "," + margin.top + ")" );

    var maxValue = max0( data.map( function( group ) {
      return max0( group.results.map( function( result ) {
        return max0( numbers( result.measurements ) );
      }));
    }));

    x.domain( [ 0, maxValue ] ).nice();
    y.domain( data.map( function( group ) { return group.name; } ) );
    y1.domain( variants ).rangeRoundBands( [0, y.rangeBand()] );
    svg.append( "g" )
        .attr( "class", "x axis" )
        .attr( "transform", "translate(0," + height + ")" )
        .call( xAxis );
    svg.append( "text" )
        .attr( "x", width + 30 )
        .attr( "y", height )
        .attr( "dy", "1em" )
        .text( opts.unit );
    svg.append( "g" )
        .attr( "class", "y axis" )
        .call( yAxis );

    var group = svg.selectAll( ".group" )
        .data( data )
      .enter().append( "g" )
        .attr( "class", "group" )
        .attr( "transform", function( d ) { return "translate(0," + y( d.name ) + ")"; } );

    var bars = group.selectAll( ".bars" )
        .data( function( d ) { return d.results; } )
      .enter().append( "g" )
        .attr( "class", "bars" );
    var drawBars = function( func, opacity ) {
      bars.append( "rect" )
          .attr( "x", x( 0 ) )
          .attr( "y", function( d ) { return y1( d.name ); } )
          .attr( "width", function( d ) { return x( func( numbers( d.measurements ) ) ); } )
          .attr( "height", y1.rangeBand() )
          .style( "opacity", opacity )
          .style( "fill", function( d ) { return color( d.name ); } );
    };
    drawBars( max0, 0.2 );
    drawBars( median0, 0.6 );
    drawBars( min0, 1 );

    if( opts.variants ) {
      var legend = svg.selectAll( ".legend" )
          .data( variants )
        .enter().append( "g" )
          .attr( "class", "legend" )
          .attr( "transform", function( d, i ) { return "translate(0," + i * 24 + ")"; } );
      legend.append( "rect" )
          .attr( "x", width + 30 )
          .attr( "y", 0 )
          .attr( "width", 8 )
          .attr( "height", 16 )
          .style( "fill", color );
      legend.append( "text" )
          .attr( "x", width + 42 )
          .attr( "y", 8 )
          .attr( "dy", ".35em" )
          .text( function( d ) { return d; } );
    }
  };

  var renderSections = function( measurements, options, sections ) {
    if( typeof sections === "string" ) {
      sections = [ sections ];
    }
    if( !sections || sections.length === 0 ) {
      benchmarks.createChart( measurements, options );
    } else {
      var name = sections[0];
      benchmarks.valuesForVariable( measurements, name ).forEach( function( value ) {
        d3.select( "body" ).append( "h2" ).text( name + ": " + value );
        var filteredMeasurements = benchmarks.filterMeasurements( measurements, name, value );
        renderSections( filteredMeasurements, options, sections.slice( 1 ) );
      });
    }
  };

  var getDetailsAsText = function( data ) {
    return d3.keys( data.details ).sort().map( function( key ) {
      return key + ": " + data.details[ key ];
    }).join( "\n" );
  };

  benchmarks.showResults = function( data, options ) {
    d3.select( "body" ).append( "h1" ).text( data.name );
    renderSections( data.measurements, options, options.sections );
    d3.select( "body" ).append( "h2" ).text( "Details" );
    d3.select( "body" ).append( "pre" ).text( getDetailsAsText( data ) );
  };

})();
