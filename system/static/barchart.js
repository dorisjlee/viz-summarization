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
    container.removeChild(container.childNodes[0])

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
                document.getElementById('interested-in').innerHTML+='<tr>'+'<td style="color:#368332">'+ node[i].id+'<td>'+ '<td style="color:#368332"> '+node[i].filterVal+'<td> '+'<tr>';
            }
            else if (totalclick[node[i].id]==2){
                //var currNode = node_dataset.get(i);
                document.getElementById('not-interested-in').innerHTML+='<tr>'+'<td style="color:#ff0000">'+ node[i].id+'<td>'+ '<td style="color:#ff0000"> '+node[i].filterVal+'<td> '+'<tr>';
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
    container.prepend(div.firstChild);
}
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
////////                   D3 implementation                                       ////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
function render_chart(i, nodeDic){
//    Dic = JSON.parse(nodeDic)

    Dic = nodeDic
    dataset = Dic[i].slice(0,Dic[i].length-1)
    console.log(dataset)
    yAxis_name = Dic[i][Dic[i].length-1]["yName"]
    title = Dic[i][Dic[i].length-1]["filter"]
    if (i==0){
        title = "root";
    }
    else{
        for(var j = 0; j<title.length;j++){
            if(title[j] == "#"){
                title = title.substr(0, j) + '\n' + title.substr(j + 1);
            }
            else if(title[j] == "$"){
                title = title.substr(0, j) + '=' + title.substr(j + 1);
            }
        }
    }

    //$("#title").html(title) 
    //alert(dataset[0][0].xAxis);
   

    // Dimensions for the chart: height, width, and space b/t the bars
    var margins = {top: 20, right: 50, bottom: 30, left: 50}
    var height = 150 - margins.left - margins.right,
        width = 120- margins.top - margins.bottom,
        barPadding = 5

    // Create a scale for the y-axis based on data
    // >> Domain - min and max values in the dataset
    // >> Range - physical range of the scale (reversed)
//    var yScale = d3.scale.linear()
//      .domain([0, d3.max(dataset, function(d){
//        return d.yAxis;
//      })])
//      .range([height, 0]);
    var yScale = d3.scale.linear()
      .domain([0, 100])
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
    current_cell = "#c"+i.toString();
    var chart = d3.select(current_cell)
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


    chart.append("g")
    .attr("transform", "translate(" + margins.left + "," + margins.top + ")")
    .selectAll(".textlabel")
    .data(dataset)
    .enter()
    .append("text")
    .attr("class", "textlabel")
    .attr("x", function(d){ return xScale(d.xAxis)-50; })
    .attr("y", function(d){ return yScale(d.yAxis)-20; })
    .text(function(d){ return Math.round((d.yAxis) * 100) / 100; })
     .style({"font-family":"Arial", "font-weight":"300"});

    // add bar chart title
    chart.append("text")
        .attr("x", (width / 2))
        .attr("y",  "-8px")
        .attr("text-anchor", "middle")
        .style("font-size", "10px")
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
/*
    function test_chart(arrayDiv){
    // On document load, call the render() function to load the graph

    for(var i=0; i < data.length; i++){
        arrayDiv[i] = document.createElement('div');
        arrayDiv[i].id = 'block' + i;
        // arrayDiv[i].innerHTML = "render_chart("+i+");$('rect').mouseenter(function(){$('#xAxis').html(this.className.animVal);$('#yAxis').html($(this).attr('id'));});"
        arrayDiv[i].innerHTML = "<div id=chart"+i+"></div>"
        render_chart(i);
    }
  }*/


   function insertChart(){
        var chartarray = JSON.parse('{\"0\": [{ \"xAxis\": \"0\", \"yAxis\":65.72734196496572},{ \"xAxis\": \"1\", \"yAxis\":34.27265803503427},{\"childrenIndex\":[1, 2, 3, 4, 5], \"populationSize\":1313, \"filter\":\"#\",\"yName\":\"id\"}],\"1\": [{ \"xAxis\": \"0\", \"yAxis\":83.31374853113984},{ \"xAxis\": \"1\", \"yAxis\":16.686251468860164},{\"childrenIndex\":[6, 7, 8], \"populationSize\":851, \"filter\":\"#sexcode$0#\",\"yName\":\"id\"}],\"2\": [{ \"xAxis\": \"0\", \"yAxis\":33.33333333333333},{ \"xAxis\": \"1\", \"yAxis\":66.66666666666666},{\"childrenIndex\":[9, 10, 11], \"populationSize\":462, \"filter\":\"#sexcode$1#\",\"yName\":\"id\"}],\"3\": [{ \"xAxis\": \"0\", \"yAxis\":40.06211180124223},{ \"xAxis\": \"1\", \"yAxis\":59.93788819875776},{\"childrenIndex\":[6, 9], \"populationSize\":322, \"filter\":\"#pc_class$1#\",\"yName\":\"id\"}],\"4\": [{ \"xAxis\": \"0\", \"yAxis\":57.49999999999999},{ \"xAxis\": \"1\", \"yAxis\":42.5},{\"childrenIndex\":[10], \"populationSize\":280, \"filter\":\"#pc_class$2#\",\"yName\":\"id\"}],\"5\": [{ \"xAxis\": \"0\", \"yAxis\":80.59071729957806},{ \"xAxis\": \"1\", \"yAxis\":19.40928270042194},{\"childrenIndex\":[8, 11], \"populationSize\":711, \"filter\":\"#pc_class$3#\",\"yName\":\"id\"}],\"6\": [{ \"xAxis\": \"0\", \"yAxis\":67.0391061452514},{ \"xAxis\": \"1\", \"yAxis\":32.960893854748605},{\"childrenIndex\":[], \"populationSize\":179, \"filter\":\"#sexcode$0#pc_class$1#\",\"yName\":\"id\"}],\"7\": [{ \"xAxis\": \"0\", \"yAxis\":88.37675350701403},{ \"xAxis\": \"1\", \"yAxis\":11.623246492985972},{\"childrenIndex\":[], \"populationSize\":499, \"filter\":\"#sexcode$0#pc_class$3#\",\"yName\":\"id\"}],\"8\": [{ \"xAxis\": \"0\", \"yAxis\":6.293706293706294},{ \"xAxis\": \"1\", \"yAxis\":93.7062937062937},{\"childrenIndex\":[], \"populationSize\":143, \"filter\":\"#sexcode$1#pc_class$1#\",\"yName\":\"id\"}],\"9\": [{ \"xAxis\": \"0\", \"yAxis\":12.149532710280374},{ \"xAxis\": \"1\", \"yAxis\":87.85046728971963},{\"childrenIndex\":[], \"populationSize\":107, \"filter\":\"#sexcode$1#pc_class$2#\",\"yName\":\"id\"}],\"10\": [{ \"xAxis\": \"0\", \"yAxis\":62.264150943396224},{ \"xAxis\": \"1\", \"yAxis\":37.735849056603776},{\"childrenIndex\":[], \"populationSize\":212, \"filter\":\"#sexcode$1#pc_class$3#\",\"yName\":\"id\"}]}');
        //var chartarray = JSON.parse('{\"0\": [{ \"xAxis\": \"y\", \"yAxis\":39.931068439192515},{ \"xAxis\": \"s\", \"yAxis\":31.462333825701627},{ \"xAxis\": \"f\", \"yAxis\":28.55736090595766},{ \"xAxis\": \"g\", \"yAxis\":0.04923682914820286},{\"childrenIndex\":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10], \"populationSize\":8124, \"filter\":\"#\",\"yName\":\"cap_surface\"}],\"1\": [{ \"xAxis\": \"y\", \"yAxis\":44.43309499489275},{ \"xAxis\": \"s\", \"yAxis\":36.057201225740556},{ \"xAxis\": \"f\", \"yAxis\":19.40755873340143},{ \"xAxis\": \"g\", \"yAxis\":0.10214504596527069},{\"childrenIndex\":[11, 13], \"populationSize\":3916, \"filter\":\"#type$p#\",\"yName\":\"cap_surface\"}],\"2\": [{ \"xAxis\": \"y\", \"yAxis\":35.741444866920155},{ \"xAxis\": \"s\", \"yAxis\":27.186311787072242},{ \"xAxis\": \"f\", \"yAxis\":37.0722433460076},{ \"xAxis\": \"g\", \"yAxis\":0.0},{\"childrenIndex\":[12, 14, 16], \"populationSize\":4208, \"filter\":\"#type$e#\",\"yName\":\"cap_surface\"}],\"3\": [{ \"xAxis\": \"y\", \"yAxis\":41.71954314720812},{ \"xAxis\": \"s\", \"yAxis\":26.015228426395936},{ \"xAxis\": \"f\", \"yAxis\":32.233502538071065},{ \"xAxis\": \"g\", \"yAxis\":0.031725888324873094},{\"childrenIndex\":[18], \"populationSize\":3152, \"filter\":\"#cap_shape$f#\",\"yName\":\"cap_surface\"}],\"4\": [{ \"xAxis\": \"y\", \"yAxis\":41.5061295971979},{ \"xAxis\": \"s\", \"yAxis\":37.3029772329247},{ \"xAxis\": \"f\", \"yAxis\":21.19089316987741},{ \"xAxis\": \"g\", \"yAxis\":0.0},{\"childrenIndex\":[], \"populationSize\":2284, \"filter\":\"#cap_color$n#\",\"yName\":\"cap_surface\"}]}')
        //var chartarray = JSON.parse('{\"0\": [{ \"xAxis\": \"0\", \"yAxis\":42.6226660904263},{ \"xAxis\": \"1\", \"yAxis\":57.3773339095737},{\"childrenIndex\":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14], \"populationSize\":448481122585, \"filter\":\"#\",\"yName\":\"slots_millis_reduces\"}],\"1\": [{ \"xAxis\": \"0\", \"yAxis\":1.2750312370501689},{ \"xAxis\": \"1\", \"yAxis\":98.72496876294983},{\"childrenIndex\":[150, 154, 224, 286, 302, 319, 320], \"populationSize\":962811784, \"filter\":\"#is_event_query$0#has_actions_tbl$1#\",\"yName\":\"slots_millis_reduces\"}],\"2\": [{ \"xAxis\": \"0\", \"yAxis\":99.07895910376558},{ \"xAxis\": \"1\", \"yAxis\":0.9210408962344133},{\"childrenIndex\":[15, 17, 39, 41, 42, 45, 46, 49, 50, 53, 54], \"populationSize\":56725332082, \"filter\":\"#is_profile_query$0#\",\"yName\":\"slots_millis_reduces\"}],\"3\": [{ \"xAxis\": \"0\", \"yAxis\":99.07895910376558},{ \"xAxis\": \"1\", \"yAxis\":0.9210408962344133},{\"childrenIndex\":[20, 22, 39, 59, 60, 63, 64, 67, 68, 71, 72], \"populationSize\":56725332082, \"filter\":\"#is_event_query$1#\",\"yName\":\"slots_millis_reduces\"}],\"4\": [{ \"xAxis\": \"0\", \"yAxis\":98.69961173952065},{ \"xAxis\": \"1\", \"yAxis\":1.3003882604793606},{\"childrenIndex\":[24, 26, 42, 44, 58, 60, 75, 76, 79, 80, 83, 84], \"populationSize\":48034401108, \"filter\":\"#has_impressions_tbl$1#\",\"yName\":\"slots_millis_reduces\"}],\"5\": [{ \"xAxis\": \"0\", \"yAxis\":3.1553026553428802},{ \"xAxis\": \"1\", \"yAxis\":96.84469734465712},{\"childrenIndex\":[582, 702, 756, 821, 822], \"populationSize\":538925956, \"filter\":\"#has_impressions_tbl$0#has_clicks_tbl$1#has_actions_tbl$1#\",\"yName\":\"slots_millis_reduces\"}],\"6\": [{ \"xAxis\": \"0\", \"yAxis\":96.452847314117},{ \"xAxis\": \"1\", \"yAxis\":3.5471526858830025},{\"childrenIndex\":[28, 30, 46, 48, 62, 64, 74, 87, 88, 91, 92], \"populationSize\":41251572982, \"filter\":\"#has_clicks_tbl$1#\",\"yName\":\"slots_millis_reduces\"}],\"7\": [{ \"xAxis\": \"0\", \"yAxis\":5.1368682341230025},{ \"xAxis\": \"1\", \"yAxis\":94.863131765877},{\"childrenIndex\":[166, 170, 230, 234, 278, 282, 327, 328, 335, 336], \"populationSize\":972570888, \"filter\":\"#has_impressions_tbl$0#has_clicks_tbl$1#\",\"yName\":\"slots_millis_reduces\"}],\"8\": [{ \"xAxis\": \"0\", \"yAxis\":66.64747521283071},{ \"xAxis\": \"1\", \"yAxis\":33.35252478716929},{\"childrenIndex\":[681, 716, 751], \"populationSize\":22322436, \"filter\":\"#is_profile_query$1#has_clicks_tbl$1#has_distinct$0#\",\"yName\":\"slots_millis_reduces\"}],\"9\": [{ \"xAxis\": \"0\", \"yAxis\":97.29957989744665},{ \"xAxis\": \"1\", \"yAxis\":2.700420102553362},{\"childrenIndex\":[32, 34, 50, 52, 66, 68, 78, 80, 86, 88, 95, 96], \"populationSize\":35621903462, \"filter\":\"#has_actions_tbl$1#\",\"yName\":\"slots_millis_reduces\"}],\"10\": [{ \"xAxis\": \"0\", \"yAxis\":87.94379871435501},{ \"xAxis\": \"1\", \"yAxis\":12.056201285644988},{\"childrenIndex\":[35, 37, 55, 69, 81, 89, 91, 93, 95], \"populationSize\":100662793441, \"filter\":\"#has_distinct$0#\",\"yName\":\"slots_millis_reduces\"}],\"11\": [{ \"xAxis\": \"0\", \"yAxis\":24.74767956154703},{ \"xAxis\": \"1\", \"yAxis\":75.25232043845297},{\"childrenIndex\":[174, 178, 326, 343, 344], \"populationSize\":1263131326, \"filter\":\"#has_impressions_tbl$0#has_actions_tbl$1#\",\"yName\":\"slots_millis_reduces\"}],\"12\": [{ \"xAxis\": \"0\", \"yAxis\":18.077638273971168},{ \"xAxis\": \"1\", \"yAxis\":81.92236172602884},{\"childrenIndex\":[1211, 1264], \"populationSize\":35356582, \"filter\":\"#is_event_query$0#has_impressions_tbl$0#has_actions_tbl$1#has_distinct$0#\",\"yName\":\"slots_millis_reduces\"}],\"13\": [{ \"xAxis\": \"0\", \"yAxis\":14.655460009094304},{ \"xAxis\": \"1\", \"yAxis\":85.3445399909057},{\"childrenIndex\":[184, 252, 296, 340, 346], \"populationSize\":621479148, \"filter\":\"#has_impressions_tbl$1#has_distinct$1#\",\"yName\":\"slots_millis_reduces\"}],\"14\": [{ \"xAxis\": \"0\", \"yAxis\":91.39887086997128},{ \"xAxis\": \"1\", \"yAxis\":8.601129130028717},{\"childrenIndex\":[621, 732, 786, 817, 821], \"populationSize\":336757495, \"filter\":\"#has_impressions_tbl$0#has_actions_tbl$1#has_distinct$0#\",\"yName\":\"slots_millis_reduces\"}],\"15\": [{ \"xAxis\": \"0\", \"yAxis\":36.80355086328234},{ \"xAxis\": \"1\", \"yAxis\":63.19644913671766},{\"childrenIndex\":[644, 762, 778], \"populationSize\":63940958, \"filter\":\"#is_event_query$1#has_impressions_tbl$0#has_clicks_tbl$1#\",\"yName\":\"slots_millis_reduces\"}],\"16\": [{ \"xAxis\": \"0\", \"yAxis\":18.077638273971168},{ \"xAxis\": \"1\", \"yAxis\":81.92236172602884},{\"childrenIndex\":[1211, 1252], \"populationSize\":35356582, \"filter\":\"#is_profile_query$1#has_impressions_tbl$0#has_actions_tbl$1#has_distinct$0#\",\"yName\":\"slots_millis_reduces\"}],\"17\": [{ \"xAxis\": \"0\", \"yAxis\":4.967143405001682},{ \"xAxis\": \"1\", \"yAxis\":95.03285659499832},{\"childrenIndex\":[200, 204, 268, 312, 336, 340, 354, 356], \"populationSize\":1478600415, \"filter\":\"#has_clicks_tbl$1#has_distinct$1#\",\"yName\":\"slots_millis_reduces\"}],\"18\": [{ \"xAxis\": \"0\", \"yAxis\":8.366873216617686},{ \"xAxis\": \"1\", \"yAxis\":91.63312678338231},{\"childrenIndex\":[208, 212, 276, 320, 344, 352, 356], \"populationSize\":1005717821, \"filter\":\"#has_actions_tbl$1#has_distinct$1#\",\"yName\":\"slots_millis_reduces\"}],\"19\": [{ \"xAxis\": \"0\", \"yAxis\":46.19324463270985},{ \"xAxis\": \"1\", \"yAxis\":53.80675536729015},{\"childrenIndex\":[1493], \"populationSize\":13836731, \"filter\":\"#is_profile_query$1#has_impressions_tbl$0#has_clicks_tbl$1#has_actions_tbl$1#has_distinct$0#\",\"yName\":\"slots_millis_reduces\"}]}')
        console.log(chartarray.length)

        var len = Object.keys(chartarray).length;
        var table = document.getElementById("charttable");

        var cell_idx = 0;
        for(cell_idx = 0; cell_idx < len; cell_idx++){
            render_chart(cell_idx,chartarray)
        }

        /*while(row=table.rows[r++])
        {
            var c=0;
            while(cell=row.cells[c++])
            {
                if (idx<len){

                      cell.innerHTML=render_chart(idx,chartarray);
                      idx++;
                }

            }
        }*/
    }

