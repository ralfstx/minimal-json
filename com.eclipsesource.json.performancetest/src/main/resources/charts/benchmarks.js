/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
var benchmarks = {};

(function(){

  // Helpers

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

  var unique = function( array ) {
    var buffer = {};
    return array.filter( function( element ) {
      var exists = element in buffer;
      buffer[ element ] = true;
      return !exists;
    });
  };

  var createQuery = function( options ) {
    return "?" + d3.keys( options ).filter( function( key ) {
      return typeof options[ key ] !== "undefined";
    }).map( function( key ) {
      return encodeURIComponent( key ) + "=" + encodeURIComponent( options[ key ] );
    }).join( '&' );
  };

  var extractUrlParameters = function() {
    var params = {};
    window.location.search.substring( 1 ).split( '&' ).forEach( function( param ) {
      var parts = param.split( '=', 2 );
      var name = decodeURIComponent( parts[0] );
      var value = decodeURIComponent( parts[1] );
      params[ name ] = value;
    });
    return params;
  };

  var applyDefault = function( func, def ) {
    return function( values ) {
      return values.length === 0 ? def : func( values );
    };
  };
  var max0 = applyDefault( d3.max, 0 );
  var min0 = applyDefault( d3.min, 0 );
  var median0 = applyDefault( d3.median, 0 );

  // View

  benchmarks.View = function( results, options ) {
    this.results = results;
    this.options = options;
    this.variables = benchmarks.extractVariables( this.results.measurements );
    var view = this;
    window.addEventListener( "popstate", function( event ) {
      view.options = event.state;
      view.show();
    });
  };

  benchmarks.View.prototype.show = function() {
    d3.select( "div#results" ).remove();
    var parent = d3.select( "body" ).append( "div" ).attr( "id", "results" );
    this._fixOptions();
    this.renderHeadline( parent );
    this.renderVariables( parent );
    this.renderSections( parent );
    this.renderDetails( parent );
  };

  benchmarks.View.prototype._fixOptions = function() {
    var view = this;
    var multiVars = this.variables.names.filter( function( name ) {
      return view.variables.values[ name ].length > 1;
    });
    if( typeof this.options.rows === "undefined" && multiVars.length > 1 ) {
      this.options.rows = multiVars[0];
    }
  };

  benchmarks.View.prototype.renderHeadline = function( parent ) {
    document.title = this.results.name;
    parent.append( "h1" ).text( this.results.name );
  };

  benchmarks.View.prototype.renderVariables = function( parent ) {
    var view = this;
    var table = parent.append( "table" ).attr( "class", "variables" );
    var th = table.append( "tr" ).attr( "class", "head" );
    th.append( "td" ).text( "name" );
    th.append( "td" ).text( "values" );
    th.append( "td" ).text( "rows" );
    th.append( "td" ).text( "groups" );
    var tr = table.selectAll( "tr.var" ).data( this.variables.names ).enter()
      .append( "tr" )
      .attr( "class", function( name ) { return "var " + name; } );
    tr.append( "td" ).text( function( name ) { return name; } );
    tr.append( "td" ).text( function( name ) { return view.variables.values[ name ].join( ", " ); } );
    tr.append( "td" ).append( "input" ).property( "type", "radio" )
      .attr( "name", "rows" )
      .property( "checked", function( name ) { return view.options.rows === name; } )
      .property( "disabled", function( name ) { return view.variables.values[ name ].length <= 1; } )
      .on( "change", function( name ) {
        view._handleChange( "rows", this.checked ? name : undefined );
      });
    tr.append( "td" ).append( "input" ).property( "type", "checkbox" )
      .attr( "class", function( name ) { return "groups " + name; } )
      .property( "checked", function( name ) { return view.options.groups === name; } )
      .property( "disabled", function( name ) { return view.variables.values[ name ].length <= 1; } )
      .on( "change", function( name ) {
        view._handleChange( "groups", this.checked ? name : undefined );
      });
  };

  benchmarks.View.prototype._handleChange = function( name, value ) {
    if( typeof value === "undefined" ) {
      delete this.options[ name ];
    } else {
      this.options[ name ] = value;
    }
    history.pushState( this.options, "", createQuery( this.options ) );
    this.show();
  };

  benchmarks.View.prototype.renderSections = function( parent, measurements, sections, level ) {
    var view = this;
    if( typeof measurements === "undefined" ) {
      measurements = this.results.measurements;
    }
    if( typeof sections === "undefined" ) {
      sections = this._getSections();
    }
    if( typeof level === "undefined" ) {
      level = 2;
    }
    var name = sections[0];
    benchmarks.extractValues( measurements, name ).forEach( function( value ) {
      parent.append( "h" + level ).text( name + ": " + value );
      var filteredMeasurements = benchmarks.filterByValue( measurements, name, value );
      if( sections.length === 1 ) {
        view.createChart( parent, filteredMeasurements );
      } else {
        var subsection = parent.append( "div" ).attr( "class", "section" );
        view.renderSections( subsection, filteredMeasurements, sections.slice( 1 ), level + 1 );
      }
    });
  };

  benchmarks.View.prototype._getSections = function() {
    var view = this;
    return this.variables.names.filter( function( name ) {
      return view.variables.values[ name ].length > 1 &&
        name !== view.options.rows &&
        name !== view.options.groups;
    });
  };

  benchmarks.View.prototype.createChart = function( parent, measurements ) {
    new benchmarks.Chart( measurements, this.options ).render( parent );
  };

  benchmarks.View.prototype.renderDetails = function( parent ) {
    var view = this;
    parent.append( "h2" ).text( "Details" );
    var table = parent.append( "table" ).attr( "class", "details" );
    var data = d3.keys( this.results.details ).sort().map( function( name ) {
      return { name: name, value: view.results.details[ name ] };
    });
    var tr = table.selectAll( "tr" ).data( data ).enter().append( "tr" );
    tr.append( "td" ).attr( "class", "head" ).text( function(d) { return d.name;} );
    tr.append( "td" ).text( function(d) { return d.value;} );
  };

  benchmarks.View.showError = function( message ) {
    d3.select( "body" ).insert( "div", ":first-child" ).attr( "class", "error" ).text( message );
  };

  // Chart

  benchmarks.Chart = function( measurements, options ) {
    this.measurements = measurements;
    this.options = mergeObjects( {
      width: 820,
      height: -1,
      margin: { top: 20, right: 170, bottom: 30, left: 150 },
      colors: measurements.length > 10 ? d3.scale.category10().range() : d3.scale.category20().range(),
      unit: benchmarks.selectBestUnit( measurements )
    }, options );
  };

  benchmarks.Chart.prototype.createSvg = function( parent ) {
    var chart = this;
    return parent.append( "svg" )
        .attr( "width", chart.width + chart.margin.left + chart.margin.right )
        .attr( "height", chart.height + chart.margin.top + chart.margin.bottom )
      .append( "g" )
        .attr( "transform", "translate(" + chart.margin.left + "," + chart.margin.top + ")" );
  };

  benchmarks.Chart.prototype.renderXAxis = function( parent ) {
    var chart = this;
    var xAxis = d3.svg.axis().scale( chart.xscale ).orient( "bottom" ).tickSize( -chart.height );
    parent.append( "g" )
        .attr( "class", "x axis" )
        .attr( "transform", "translate(0," + chart.height + ")" )
        .call( xAxis );
    parent.append( "text" )
        .attr( "x", chart.width + 30 )
        .attr( "y", chart.height )
        .attr( "dy", "1em" )
        .text( chart.options.unit );
  };

  benchmarks.Chart.prototype.renderYAxis = function( parent ) {
    var chart = this;
    var yAxis = d3.svg.axis().scale( chart.yscale ).orient( "left" );
    parent.append( "g" )
        .attr( "class", "y axis" )
        .call( yAxis );
  };

  benchmarks.Chart.prototype.createScales = function() {
    var chart = this;
    var maxValue = max0( benchmarks.getValues( chart.measurements, chart.options.unit ) );
    chart.xscale = d3.scale.linear().range( [0, chart.width] )
        .domain( [ 0, maxValue ] ).nice();
    chart.yscale = d3.scale.ordinal().rangeRoundBands( [0, chart.height], 0.1 )
        .domain( chart.rows );
    chart.y1scale = d3.scale.ordinal()
        .domain( chart.groups ).rangeRoundBands( [0, chart.yscale.rangeBand()] );
  };

  benchmarks.Chart.prototype.createRows = function( parent ) {
    var chart = this;
    return parent.selectAll( "g.row" )
      .data( this.rows.map( function( row ) {
        return {
          name: row,
          results: benchmarks.filterByValue( chart.measurements, chart.options.rows, row )
        };
      }))
    .enter().append( "g" )
      .attr( "class", "row" )
      .attr( "transform", function( d ) { return "translate(0," + chart.yscale( d.name ) + ")"; } );
  };

  benchmarks.Chart.prototype.createGroups = function( parent ) {
    var chart = this;
    var groups = parent.selectAll( "g.group" )
      .data( function( d ) {
        return chart.groups.map( function( group ) {
          return {
            name: group,
            results: benchmarks.filterByValue( d.results, chart.options.groups, group )
          };
        });
      })
    .enter().append( "g" )
      .attr( "class", "group" );
    this.createBars( groups );
  };

  benchmarks.Chart.prototype.createBars = function( parent ) {
    this.createBar( parent, max0, 0.2 );
    this.createBar( parent, median0, 0.6 );
    this.createBar( parent, min0, 1 );
  };

  benchmarks.Chart.prototype.createBar = function( parent, func, opacity ) {
    var chart = this;
    parent.append( "rect" )
      .attr( "class", function( d, i ) { return "bar col" + i + " " + d.name; } )
      .attr( "x", chart.xscale( 0 ) )
      .attr( "y", function( d ) { return chart.y1scale( d.name ); } )
      .attr( "width", function( d ) {
        return chart.xscale( func( benchmarks.getValues( d.results, chart.options.unit ) ) );
      })
      .attr( "height", chart.y1scale.rangeBand() )
      .style( "opacity", opacity );
  };

  benchmarks.Chart.prototype.render = function( parent ) {
    var chart = this;
    var hasRows = typeof this.options.rows !== 'undefined';
    this.rows = hasRows ? benchmarks.extractValues( chart.measurements, this.options.rows )
                        : [ undefined ];
    var hasGroups = typeof this.options.groups !== 'undefined';
    this.groups = hasGroups ? benchmarks.extractValues( chart.measurements, this.options.groups )
                            : [ undefined ];
    this.margin = this.options.margin;
    this.width = this.options.width - this.margin.left - this.margin.right;
    this.height = this._calculateHeight() - this.margin.top - this.margin.bottom;
    var svg = this.createSvg( parent );
    this.createScales();
    this.renderXAxis( svg );
    this.renderYAxis( svg );
    var rows = this.createRows( svg );
    if( hasGroups ) {
      this.createGroups( rows );
      this.createLegend( svg );
    } else {
      this.createBars( rows );
    }
  };

  benchmarks.Chart.prototype._calculateHeight = function() {
    if( this.options.height !== -1 ) {
      return this.options.height;
    }
    var groupHeight = Math.max( this.groups.length * 10, 20 );
    var barsHeight = this.rows.length * groupHeight;
    var padding = ( this.rows.length + 1 ) * groupHeight * 0.1;
    return barsHeight + padding + this.options.margin.top + this.options.margin.bottom;
  };

  benchmarks.Chart.prototype.createLegend = function( parent ) {
    var chart = this;
    var legend = parent.selectAll( ".legend" )
        .data( chart.groups )
      .enter().append( "g" )
        .attr( "class", "legend" )
        .attr( "transform", function( d, i ) { return "translate(0," + i * 24 + ")"; } );
    legend.append( "rect" )
        .attr( "class", function( d, i ) { return "bar col" + i + " " + d; } )
        .attr( "x", chart.width + 30 )
        .attr( "y", 0 )
        .attr( "width", 8 )
        .attr( "height", 16 );
    legend.append( "text" )
        .attr( "x", chart.width + 42 )
        .attr( "y", 8 )
        .attr( "dy", ".35em" )
        .text( function( d ) { return d; } );
  };

  // statics

  benchmarks.extractVariables = function( measurements ) {
    var names = unique( d3.merge( measurements.map( function( m ) {
      return d3.keys( m.variables );
    })));
    var values = {};
    names.forEach( function( name ) {
      values[ name ] = benchmarks.extractValues( measurements, name );
    });
    return { names : names, values : values };
  };

  benchmarks.extractValues = function( measurements, name ) {
    return unique( measurements.map( function( m ) {
      return m.variables[ name ];
    }).filter( function( value ) {
      return typeof value !== "undefined";
    }));
  };

  benchmarks.filterByValue = function( measurements, name, value ) {
    return measurements.filter( function( m ) {
      return typeof name === "undefined" || m.variables[ name ] === value;
    });
  };

  benchmarks.getValues = function( measurements, unit ) {
    return d3.merge( measurements.map( function( m ) {
      return m.values.map( function( value ) {
        return value / m.units[ unit ];
      });
    }));
  };

  benchmarks.selectBestUnit = function( measurements ) {
    // TODO ensure same units in all measurements
    var max = max0( d3.merge( measurements.map( function( m ) {
      return m.values;
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

  benchmarks.init = function( options ) {
    var params = extractUrlParameters();
    var input = params.input;
    if( input ) {
      d3.select( "input[name='input']" ).property( "value", input );
      d3.json( input, function( error, results ) {
        if( error ) {
          benchmarks.View.howError( "Failed to load benchmark: " + input );
        } else {
          new benchmarks.View( results, mergeObjects( {
            input: input,
            rows: params.rows,
            groups: params.groups
          }, options )).show();
        }
      });
    }
  };

})();
