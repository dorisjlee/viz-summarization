function render_chart(i){
    // Golden Snowglobe totals (as of 2/5/15)

    dataset = Dic[i].slice(0, Dic[i].length-1)

    yAxis_name = Dic[i][Dic[i].length-1]["yName"]

    title = Dic[i][Dic[i].length-1]["filter"]

    //$("#title").html(title) 
    //alert(dataset[0][0].xAxis);
   

    // Dimensions for the chart: height, width, and space b/t the bars
    var margins = {top: 30, right: 50, bottom: 30, left: 50}
    var height = 300 - margins.left - margins.right,
        width = 450 - margins.top - margins.bottom,
        barPadding = 5

    // Create a scale for the y-axis based on data
    // >> Domain - min and max values in the dataset
    // >> Range - physical range of the scale (reversed)
    var yScale = d3.scale.linear()
      .domain([0, d3.max(dataset, function(d){
        return d.yAxis;
      })])
      .range([height, 0]);

    // Implements the scale as an actual axis
    // >> Orient - places the axis on the left of the graph
    // >> Ticks - number of points on the axis, automated
    var yAxis = d3.svg.axis()
      .scale(yScale)
      .orient('left')
      .ticks(5);

    // Creates a scale for the x-axis based on city names
    var xScale = d3.scale.ordinal()
      .domain(dataset.map(function(d){
        return d.xAxis;
      }))
      .rangeRoundBands([0, width], .1);

    // Creates an axis based off the xScale properties
    var xAxis = d3.svg.axis()
      .scale(xScale)
      .orient('bottom');

    // Creates the initial space for the chart
    // >> Select - grabs the empty <div> above this script
    // >> Append - places an <svg> wrapper inside the div
    // >> Attr - applies our height & width values from above
    var chart = d3.select('#chart'+i)
      .append('svg')
      .attr('width', width + margins.left + margins.right)
      .attr('height', height + margins.top + margins.bottom)
      .append('g')
      .attr('transform', 'translate(' + margins.left + ',' + margins.top + ')');
    
    chart.selectAll("rect")
          .data(dataset)

    // For each value in our dataset, places and styles a bar on the chart

    // Step 1: selectAll.data.enter.append
    // >> Loops through the dataset and appends a rectangle for each value
    chart.selectAll('rect')
      .data(dataset)
      .enter()
      .append('rect')
      // Step 2: X & Y
      // >> X - Places the bars in horizontal order, based on number of
      //        points & the width of the chart
      // >> Y - Places vertically based on scale
      .attr('x', function(d, i){
        return xScale(d.xAxis);
      })
      .attr('y', function(d){
        return yScale(d.yAxis);
      })

      // Step 3: Height & Width
      // >> Width - Based on barpadding and number of points in dataset
      // >> Height - Scale and height of the chart area
      .attr('width', (width / dataset.length) - barPadding)
      .attr('height', function(d){
        return height - yScale(d.yAxis);
      })
      .attr('fill', 'steelblue')

      // Step 4: Info for hover interaction
      .attr('class', function(d){
        return d.xAxis;
      })
      .attr('id', function(d){
        return d.yAxis;
      });

    // Renders the yAxis once the chart is finished
    // >> Moves it to the left 10 pixels so it doesn't overlap
    chart.append('g')
      .attr('class', 'axis')
      .attr('transform', 'translate(-10, 0)')
      .call(yAxis);

    // Appends the yAxis
    chart.append('g')
      .attr('class', 'axis')
      .attr('transform', 'translate(0,' + (height + 10) + ')')
      .call(xAxis);

    // Adds yAxis title
    chart.append('text')
      .text(yAxis_name)
      .attr('transform', 'translate(-30, -20)');
  }
  function handleMouseOver() {
    console.log("alert");
    //alert("alert");
  }
  /**function test_chart(){
    // On document load, call the render() function to load the graph

    for (i = 0; i < data.length; i++) 
    { 
        render_chart(i);
        $('rect').mouseenter(function(){
        $('#xAxis').html(this.className.animVal);
        $('#yAxis').html($(this).attr('id'));
      });
    }**/

    function test_chart(arrayDiv){
    // On document load, call the render() function to load the graph

    for(var i=0; i < data.length; i++){
        arrayDiv[i] = document.createElement('div');
        arrayDiv[i].id = 'block' + i;
        // arrayDiv[i].innerHTML = "render_chart("+i+");$('rect').mouseenter(function(){$('#xAxis').html(this.className.animVal);$('#yAxis').html($(this).attr('id'));});"
        arrayDiv[i].innerHTML = "<div id=chart"+i+"></div>"
        render_chart(i);
    }
  }
    
    
    
  
  