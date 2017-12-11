var network = null;
var DIR = 'img/refresh-cl/';
var LENGTH_MAIN = 150;
var LENGTH_SUB = 50;
var node_dataset = null;
// Called when the Visualization API is loaded.
var totalclick = {};
var options;
function draw(node,edge) {
    // create a network
    var container = document.getElementById('mynetwork');

    //(totalclick = []).length = node.length;
    //totalclick.fill(0);

    node_dataset = new vis.DataSet(node);
    for (i = 0; i < node.length; i++){

            totalclick[node[i].id] = 0;

    }
    console.log(totalclick);
    var data = {
        nodes: node_dataset,
        edges: edge
    };
    options = {

        interaction: {
            selectable: true
        },
        nodes: {
          chosen:false,
          borderWidth:3,
          size: 100,
          color: {
              border: '#A1BACB',
              background: '#FFFFFF',
              highlight: 'red',
            },
          font:{color:'#0B131A',
                size:8
          },
          shapeProperties: {
              useBorderWithImage:true
            }

        },
        physics: {enabled: false},
        edges: {smooth: false,
                color: '4E7189'
        },
        layout: {
            randomSeed: undefined,
            improvedLayout:true,
            hierarchical: {
              enabled:true,
              levelSeparation: 300,
              nodeSpacing: 300,
              treeSpacing: 600,
              blockShifting: true,
              edgeMinimization: true,
              parentCentralization: true,
              sortMethod: 'directed'   // hubsize, directed
            }
        }
    };


    console.log(node_dataset);
    network = new vis.Network(container, data, options);
    network.on("click", function(params) {

        var nodeID = params['nodes']['0'];
        console.log(nodeID);

        totalclick[nodeID] = (totalclick[nodeID]+1)%3;
        if(totalclick[nodeID]==0)
            totalclick[nodeID] = 3;
        var color;
        if(totalclick[nodeID]==1)
            color = 'grey';
        else if(totalclick[nodeID]==2)
            color = 'red';
        else if(totalclick[nodeID]==3)
            color = 'green';

        if (nodeID>=0) {
            var clickedNode = node_dataset.get(nodeID);
            //node_dataset.remove(nodeID);
            console.log(clickedNode);
            console.log(totalclick);


            clickedNode.color = {
                border: color,
                highlight:color
            }

            //console.log("before: ")
            //console.log(node_dataset);
            node_dataset.update(clickedNode);
            //console.log("after: ")
            //console.log(node_dataset);
        }

        $.post("/getInterested",{
            "interested" : JSON.stringify(totalclick),
            "fname" : JSON.stringify(fname)
        },'application/json')
        document.getElementById('interested-in').innerHTML = '';
        document.getElementById('not-interested-in').innerHTML = '';
        for (i = 0; i < node.length; i++) {

            if(totalclick[node[i].id]==3){
                //var currNode = node_dataset.get(i);
                document.getElementById('interested-in').innerHTML+='<tr>'+'<td style="color:#368332">'+ node[i].id+'<td>'+ '<td style="padding-left:1cm;color:#368332"> '+node[i].filterVal+'<td> '+'<tr>';
            }
            else if (totalclick[node[i].id]==2){
                //var currNode = node_dataset.get(i);
                document.getElementById('not-interested-in').innerHTML+='<tr>'+'<td style="color:#ff0000">'+ node[i].id+'<td>'+ '<td style="padding-left:1cm;color:#ff0000"> '+node[i].filterVal+'<td> '+'<tr>';
            }
        }

    });

   /*network.on("click", function (params) {
        console.log(params)
       params.event = "[original event]";
       //document.getElementById('eventSpan').innerHTML = '<h2>Click event:</h2>' + JSON.stringify(params, null, 4);
       console.log('click event, getNodeAt returns: ' + this.getNodeAt(params.pointer.DOM));
        //var temp = data.get(this.getNodeAt(params.pointer.DOM));
        //params.nodes.update([{id:1, color:{background:'#0B131A'}}]);
    });*/
    var div = document.createElement('div')
    div.innerHTML="<img src='resources/Eclipse.svg' id = 'loadingDashboard' style='display: none; position: relative; z-index: 10; width: 100%; height: 50%;'>"
    container.append(div.firstChild);
}
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
////////                   D3 implementation                                       ////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
function render_chart(i, nodeDic){
//    Dic = JSON.parse(nodeDic)
    Dic = nodeDic
    dataset = Dic[i].slice(0, Dic[i].length-1)
    yAxis_name = Dic[i][Dic[i].length-1]["yName"]
    title = Dic[i][Dic[i].length-1]["filter"]

    //$("#title").html(title) 
    //alert(dataset[0][0].xAxis);
   

    // Dimensions for the chart: height, width, and space b/t the bars
    var margins = {top: 30, right: 50, bottom: 30, left: 50}
    var height = 300 - margins.left - margins.right,
        width = 450 - margins.top - margins.bottom,
        barPadding = 25

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

    // add bar chart title
    chart.append("text")
        .attr("x", (width / 2))
        .attr("y",  "-8px")
        .attr("text-anchor", "middle")
        .style("font-size", "16px")
        .style("text-decoration", "underline")
        .text(title);

   return chart;
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