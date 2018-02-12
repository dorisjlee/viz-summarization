function constructQueryWithArgs(dataset,xAxis,algorithm,metric,ic,ip,k){
    query = {
        "dataset": dataset,
        "xAxis": xAxis,
        "algorithm": algorithm,
        "metric": metric,
        "ic":parseFloat(ic).toFixed(1),
        "ip": parseFloat(ip).toFixed(1),
        "k": k,
        "method": "query"
    }
    console.log(ic)
    readDashboardOutput(query)
    return query
}
function constructQuery(){
    query = {
        "dataset": $("#all_tables").val() || "",
        "xAxis": $("#xaxis").val() || "",
        "yAxis": $("#yaxis").val() || "",
        "aggFunc": $("input[name='aggFunc']:checked").val() || "",
        "algorithm": $("#algorithm").val() || "",
        "metric": $("#metric").val() || "",
        "filters": JSON.stringify(filters) ,
        "ic":parseFloat($("#ic").val()).toFixed(1)|| "",
        "ip":parseFloat($("#ip").val()).toFixed(1)|| "",
        "k":$("#k").val()|| "",
        "method": "query"
    }
    readDashboardOutput(query)
    return query
}
function readDashboardOutput(query){
    fname = query["dataset"]+"_"+query["xAxis"]+"_"+query["algorithm"]
            +"_"+query["metric"]+"_ic"+query["ic"]+"_ip"+query["ip"]+"_k"+query["k"]+".json"
    // Data Upload after options selection
    console.log(fname)
    var nodeDic = ""
    var json_pathloc = "http://"+window.location.hostname+":"+window.location.port+"/generated_dashboards/"+fname
    $.ajax({
        url: json_pathloc,
        type: "GET",
        dataType: "text",
        success: function(data) {
            getNodeEdgeListThenDraw(data);
        }
    })
    var div = document.getElementById("additionalInfoPanel")
    div.innerHTML = "<a href=\""+json_pathloc+"\">"+"filename:"+fname+"</a>"
}
// function constructQueryCallback(){
//     var query = constructQuery();
//     $.post('/postQuery', query ,'application/json')
//     .success(
//         function(data){
//             // results = JSON.parse(data);
//             console.log(data)
//             // Do stuff 
//     })
// }

function populateOptions(list,selector)
{
    selector.innerHTML="";
    //selector.options[0] = new Option("None","None");
    for (var i=0;i<list.length;i++){
      selector.options[i+1] = new Option(list[i],list[i]);
    }
}
// Proper way of actually reading from a DB
// var columns =[];
// $("#all_tables").change(function (){
//     $.post('/getColumns',{
//         "tablename": $("#all_tables").val()
//     },'application/json').success( function(data){
//         // console.log(data)
//         columns = JSON.parse(data);
//         console.log(columns)
//         populateOptions(columns,document.getElementById("xaxis"));
//         populateOptions(columns,document.getElementById("yaxis"));
//         constructQueryCallback()
//     })
// })
// $("#xaxis").change(constructQueryCallback)
// $("#yaxis").change(constructQueryCallback)
// $("input[name='aggFunc']").change(constructQueryCallback)
var columns =[];
var slider_k_input = document.getElementById('slider-k-input'),
   slider_k_output = document.getElementById('slider-k-output');

var availableDict;
$("#all_tables").change(function (){
    $.post('/getAvailableFiles',{
        "tablename": $("#all_tables").val()
    },'application/json').success( function(data){
        console.log(data)
        availableDict = data;
        populateOptions(data["xAxis"],document.getElementById("xaxis"));
        populateOptions(data["dist"],document.getElementById("metric"));
        populateOptions(data["algo"],document.getElementById("algorithm"));
        populateSlider('k',data);
        populateSlider('ic',data);
        populateSlider('ip',data);
    })
})

function populateSlider(name,data){
    var slider_input = document.getElementById(name),
        slider_output = document.getElementById(name);
        slider_input.oninput = function(){
            document.getElementById(name).max=data[name].length - 1;
            slider_output.innerHTML = data[name][this.value];
        };
        slider_input.oninput();
}

//var values = [1,3,5,10,20,50,100];    //values for k
//var slider_k_input = document.getElementById('slider-k-input'),
   //slider_k_output = document.getElementById('slider-k-output');





function mymenuicon(x) {
    x.classList.toggle("change");
}

// $("#submit").click(constructQuery)
// $("#xaxis").change(constructQuery)
// $("input[name='aggFunc']").change(constructQuery)
function getNodeEdgeListThenDraw(nodeDicStr){
    var jsonClean = true
    if (typeof(nodeDicStr)=="object"){
        jsonClean = false
    }
    document.getElementById("loadingDashboard").style.display = "inline"
    $.post("/getNodeEdgeList",{
        "nodeDic" : JSON.stringify(nodeDicStr),
        "jsonClean":jsonClean
    },'application/json').success(function(data){
        edgeList = data[0];
        nodeList = data[1];
        console.log("edgeList:")
        console.log(edgeList)
        console.log("nodeList:")
        console.log(nodeList)
        draw(nodeList,edgeList)
        document.getElementById("loadingDashboard").style.display = "none";
    })
}

// Get the modal
var modal = document.getElementById('message');

// Get the button that opens the modal
var btn = document.getElementById("helpmsg");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close")[0];

// When the user clicks on the button, open the modal
btn.onclick = function() {
    modal.style.display = "block";
}

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
    modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

function IsJsonString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}

function toggleCanvas(element)
{
    if (element.checked){
        document.getElementById('mynetwork').style.display = 'none';
        document.getElementById('mynetwork2').style.display = '';
        document.getElementById('right-sidebar').style.display = 'none';
        document.getElementById('right-sidebar2').style.display = '';
    }
    else{
        document.getElementById('mynetwork').style.display = '';
        document.getElementById('mynetwork2').style.display = 'none';
        document.getElementById('right-sidebar').style.display = '';
        document.getElementById('right-sidebar2').style.display = 'none';
    }
}


// Direct input graphDic submission form 
$("#graphDicSubmit").click(function(){
    getNodeEdgeListThenDraw($("#graphDic").val());
})
// Trump Clinton example (display by default, on startup)
// getNodeEdgeListThenDraw({'1': [{'xAxis': 'Clinton', 'yAxis': 48}, {'xAxis': 'Trump', 'yAxis': 46}, {'xAxis': 'Others', 'yAxis': 6}, {'filter': 'All', 'childrenIndex': [2, 3], 'yName': '% of vote'}], '2': [{'xAxis': 'Clinton', 'yAxis': 31}, {'xAxis': 'Trump', 'yAxis': 62}, {'xAxis': 'Others', 'yAxis': 7}, {'filter': 'Race = White', 'childrenIndex': [4, 5], 'yName': '% of vote'}], '3': [{'xAxis': 'Clinton', 'yAxis': 21}, {'xAxis': 'Trump', 'yAxis': 70}, {'xAxis': 'Others', 'yAxis': 9}, {'filter': 'Gender = F', 'childrenIndex': [], 'yName': '% of vote'}], '4': [{'xAxis': 'Clinton', 'yAxis': 21}, {'xAxis': 'Trump', 'yAxis': 52}, {'xAxis': 'Others', 'yAxis': 27}, {'filter': 'Color = Blue', 'childrenIndex': [5], 'yName': '% of vote'}], '5': [{'xAxis': 'Clinton', 'yAxis': 20}, {'xAxis': 'Trump', 'yAxis': 30}, {'xAxis': 'Others', 'yAxis': 50}, {'filter': 'Job = Student', 'childrenIndex': [], 'yName': '% of vote'}]})
// Titanic Default Example
fname = "default"
getNodeEdgeListThenDraw({"0": [{ "xAxis": "0", "yAxis":65.72734196496572},{ "xAxis": "1", "yAxis":34.27265803503427},{"childrenIndex":[1, 2, 3, 4, 5], "populationSize":1313, "filter":"#","yName":"id"}],"1": [{ "xAxis": "0", "yAxis":83.31374853113984},{ "xAxis": "1", "yAxis":16.686251468860164},{"childrenIndex":[6, 7, 8], "populationSize":851, "filter":"#sexcode$0#","yName":"id"}],"2": [{ "xAxis": "0", "yAxis":33.33333333333333},{ "xAxis": "1", "yAxis":66.66666666666666},{"childrenIndex":[9, 10, 11], "populationSize":462, "filter":"#sexcode$1#","yName":"id"}],"3": [{ "xAxis": "0", "yAxis":40.06211180124223},{ "xAxis": "1", "yAxis":59.93788819875776},{"childrenIndex":[6, 9], "populationSize":322, "filter":"#pc_class$1#","yName":"id"}],"4": [{ "xAxis": "0", "yAxis":57.49999999999999},{ "xAxis": "1", "yAxis":42.5},{"childrenIndex":[7, 10], "populationSize":280, "filter":"#pc_class$2#","yName":"id"}],"5": [{ "xAxis": "0", "yAxis":80.59071729957806},{ "xAxis": "1", "yAxis":19.40928270042194},{"childrenIndex":[8, 11], "populationSize":711, "filter":"#pc_class$3#","yName":"id"}],"6": [{ "xAxis": "0", "yAxis":67.0391061452514},{ "xAxis": "1", "yAxis":32.960893854748605},{"childrenIndex":[], "populationSize":179, "filter":"#sexcode$0#pc_class$1#","yName":"id"}],"7": [{ "xAxis": "0", "yAxis":85.54913294797689},{ "xAxis": "1", "yAxis":14.450867052023122},{"childrenIndex":[], "populationSize":173, "filter":"#sexcode$0#pc_class$2#","yName":"id"}],"8": [{ "xAxis": "0", "yAxis":88.37675350701403},{ "xAxis": "1", "yAxis":11.623246492985972},{"childrenIndex":[], "populationSize":499, "filter":"#sexcode$0#pc_class$3#","yName":"id"}],"9": [{ "xAxis": "0", "yAxis":6.293706293706294},{ "xAxis": "1", "yAxis":93.7062937062937},{"childrenIndex":[], "populationSize":143, "filter":"#sexcode$1#pc_class$1#","yName":"id"}],"10": [{ "xAxis": "0", "yAxis":12.149532710280374},{ "xAxis": "1", "yAxis":87.85046728971963},{"childrenIndex":[], "populationSize":107, "filter":"#sexcode$1#pc_class$2#","yName":"id"}],"11": [{ "xAxis": "0", "yAxis":62.264150943396224},{ "xAxis": "1", "yAxis":37.735849056603776},{"childrenIndex":[], "populationSize":212, "filter":"#sexcode$1#pc_class$3#","yName":"id"}]})


//mode2 functions
function CreateRow() {
    var table = document.getElementById("FilterTable");
    var rowCount = table.rows.length;
    var row = table.insertRow(rowCount);
    var cell1 = row.insertCell(0);

    cell1.innerHTML = "NEW CELL1";

}

function DeleteRow() {
	var table = document.getElementById("FilterTable");
    var rowCount = table.rows.length;
    table.deleteRow(rowCount-1);
}

function showfilter(cell){

     //var Dic = JSON.parse('{\"0\": [{ \"xAxis\": \"0\", \"yAxis\":65.72734196496572},{ \"xAxis\": \"1\", \"yAxis\":34.27265803503427},{\"childrenIndex\":[1, 2, 3, 4, 5], \"populationSize\":1313, \"filter\":\"#\",\"yName\":\"id\"}],\"1\": [{ \"xAxis\": \"0\", \"yAxis\":83.31374853113984},{ \"xAxis\": \"1\", \"yAxis\":16.686251468860164},{\"childrenIndex\":[6, 7, 8], \"populationSize\":851, \"filter\":\"#sexcode$0#\",\"yName\":\"id\"}],\"2\": [{ \"xAxis\": \"0\", \"yAxis\":33.33333333333333},{ \"xAxis\": \"1\", \"yAxis\":66.66666666666666},{\"childrenIndex\":[9, 10, 11], \"populationSize\":462, \"filter\":\"#sexcode$1#\",\"yName\":\"id\"}],\"3\": [{ \"xAxis\": \"0\", \"yAxis\":40.06211180124223},{ \"xAxis\": \"1\", \"yAxis\":59.93788819875776},{\"childrenIndex\":[6, 9], \"populationSize\":322, \"filter\":\"#pc_class$1#\",\"yName\":\"id\"}],\"4\": [{ \"xAxis\": \"0\", \"yAxis\":57.49999999999999},{ \"xAxis\": \"1\", \"yAxis\":42.5},{\"childrenIndex\":[10], \"populationSize\":280, \"filter\":\"#pc_class$2#\",\"yName\":\"id\"}],\"5\": [{ \"xAxis\": \"0\", \"yAxis\":80.59071729957806},{ \"xAxis\": \"1\", \"yAxis\":19.40928270042194},{\"childrenIndex\":[8, 11], \"populationSize\":711, \"filter\":\"#pc_class$3#\",\"yName\":\"id\"}],\"6\": [{ \"xAxis\": \"0\", \"yAxis\":67.0391061452514},{ \"xAxis\": \"1\", \"yAxis\":32.960893854748605},{\"childrenIndex\":[], \"populationSize\":179, \"filter\":\"#sexcode$0#pc_class$1#\",\"yName\":\"id\"}],\"7\": [{ \"xAxis\": \"0\", \"yAxis\":88.37675350701403},{ \"xAxis\": \"1\", \"yAxis\":11.623246492985972},{\"childrenIndex\":[], \"populationSize\":499, \"filter\":\"#sexcode$0#pc_class$3#\",\"yName\":\"id\"}],\"8\": [{ \"xAxis\": \"0\", \"yAxis\":6.293706293706294},{ \"xAxis\": \"1\", \"yAxis\":93.7062937062937},{\"childrenIndex\":[], \"populationSize\":143, \"filter\":\"#sexcode$1#pc_class$1#\",\"yName\":\"id\"}],\"9\": [{ \"xAxis\": \"0\", \"yAxis\":12.149532710280374},{ \"xAxis\": \"1\", \"yAxis\":87.85046728971963},{\"childrenIndex\":[], \"populationSize\":107, \"filter\":\"#sexcode$1#pc_class$2#\",\"yName\":\"id\"}],\"10\": [{ \"xAxis\": \"0\", \"yAxis\":62.264150943396224},{ \"xAxis\": \"1\", \"yAxis\":37.735849056603776},{\"childrenIndex\":[], \"populationSize\":212, \"filter\":\"#sexcode$1#pc_class$3#\",\"yName\":\"id\"}]}');
    //var Dic = JSON.parse('{\"0\": [{ \"xAxis\": \"y\", \"yAxis\":39.931068439192515},{ \"xAxis\": \"s\", \"yAxis\":31.462333825701627},{ \"xAxis\": \"f\", \"yAxis\":28.55736090595766},{ \"xAxis\": \"g\", \"yAxis\":0.04923682914820286},{\"childrenIndex\":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10], \"populationSize\":8124, \"filter\":\"#\",\"yName\":\"cap_surface\"}],\"1\": [{ \"xAxis\": \"y\", \"yAxis\":44.43309499489275},{ \"xAxis\": \"s\", \"yAxis\":36.057201225740556},{ \"xAxis\": \"f\", \"yAxis\":19.40755873340143},{ \"xAxis\": \"g\", \"yAxis\":0.10214504596527069},{\"childrenIndex\":[11, 13], \"populationSize\":3916, \"filter\":\"#type$p#\",\"yName\":\"cap_surface\"}],\"2\": [{ \"xAxis\": \"y\", \"yAxis\":35.741444866920155},{ \"xAxis\": \"s\", \"yAxis\":27.186311787072242},{ \"xAxis\": \"f\", \"yAxis\":37.0722433460076},{ \"xAxis\": \"g\", \"yAxis\":0.0},{\"childrenIndex\":[12, 14, 16], \"populationSize\":4208, \"filter\":\"#type$e#\",\"yName\":\"cap_surface\"}],\"3\": [{ \"xAxis\": \"y\", \"yAxis\":41.71954314720812},{ \"xAxis\": \"s\", \"yAxis\":26.015228426395936},{ \"xAxis\": \"f\", \"yAxis\":32.233502538071065},{ \"xAxis\": \"g\", \"yAxis\":0.031725888324873094},{\"childrenIndex\":[18], \"populationSize\":3152, \"filter\":\"#cap_shape$f#\",\"yName\":\"cap_surface\"}],\"4\": [{ \"xAxis\": \"y\", \"yAxis\":41.5061295971979},{ \"xAxis\": \"s\", \"yAxis\":37.3029772329247},{ \"xAxis\": \"f\", \"yAxis\":21.19089316987741},{ \"xAxis\": \"g\", \"yAxis\":0.0},{\"childrenIndex\":[], \"populationSize\":2284, \"filter\":\"#cap_color$n#\",\"yName\":\"cap_surface\"}]}')
    var Dic = JSON.parse('{\"0\": [{ \"xAxis\": \"p\", \"yAxis\":48.20285573609059},{ \"xAxis\": \"e\", \"yAxis\":51.7971442639094},{\"childrenIndex\":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11], \"populationSize\":8124, \"filter\":\"#\",\"yName\":\"type\"}],\"1\": [{ \"xAxis\": \"p\", \"yAxis\":46.71772428884026},{ \"xAxis\": \"e\", \"yAxis\":53.282275711159734},{\"childrenIndex\":[], \"populationSize\":3656, \"filter\":\"#cap_shape$x#\",\"yName\":\"type\"}],\"2\": [{ \"xAxis\": \"p\", \"yAxis\":49.36548223350254},{ \"xAxis\": \"e\", \"yAxis\":50.63451776649747},{\"childrenIndex\":[], \"populationSize\":3152, \"filter\":\"#cap_shape$f#\",\"yName\":\"type\"}],\"3\": [{ \"xAxis\": \"p\", \"yAxis\":53.637484586929716},{ \"xAxis\": \"e\", \"yAxis\":46.362515413070284},{\"childrenIndex\":[14], \"populationSize\":3244, \"filter\":\"#cap_surface$y#\",\"yName\":\"type\"}],\"4\": [{ \"xAxis\": \"p\", \"yAxis\":55.24256651017214},{ \"xAxis\": \"e\", \"yAxis\":44.75743348982786},{\"childrenIndex\":[15], \"populationSize\":2556, \"filter\":\"#cap_surface$s#\",\"yName\":\"type\"}],\"5\": [{ \"xAxis\": \"p\", \"yAxis\":32.758620689655174},{ \"xAxis\": \"e\", \"yAxis\":67.24137931034483},{\"childrenIndex\":[], \"populationSize\":2320, \"filter\":\"#cap_surface$f#\",\"yName\":\"type\"}],\"6\": [{ \"xAxis\": \"p\", \"yAxis\":44.65849387040281},{ \"xAxis\": \"e\", \"yAxis\":55.34150612959719},{\"childrenIndex\":[], \"populationSize\":2284, \"filter\":\"#cap_color$n#\",\"yName\":\"type\"}],\"7\": [{ \"xAxis\": \"p\", \"yAxis\":43.913043478260875},{ \"xAxis\": \"e\", \"yAxis\":56.086956521739125},{\"childrenIndex\":[], \"populationSize\":1840, \"filter\":\"#cap_color$g#\",\"yName\":\"type\"}],\"8\": [{ \"xAxis\": \"p\", \"yAxis\":18.48341232227488},{ \"xAxis\": \"e\", \"yAxis\":81.51658767772511},{\"childrenIndex\":[], \"populationSize\":3376, \"filter\":\"#bruises$t#\",\"yName\":\"type\"}],\"9\": [{ \"xAxis\": \"p\", \"yAxis\":69.33445661331086},{ \"xAxis\": \"e\", \"yAxis\":30.66554338668913},{\"childrenIndex\":[12, 13, 14, 15], \"populationSize\":4748, \"filter\":\"#bruises$f#\",\"yName\":\"type\"}],\"10\": [{ \"xAxis\": \"p\", \"yAxis\":3.4013605442176873},{ \"xAxis\": \"e\", \"yAxis\":96.5986394557823},{\"childrenIndex\":[16], \"populationSize\":3528, \"filter\":\"#odor$n#\",\"yName\":\"type\"}],\"11\": [{ \"xAxis\": \"p\", \"yAxis\":100.0},{ \"xAxis\": \"e\", \"yAxis\":0.0},{\"childrenIndex\":[17], \"populationSize\":2160, \"filter\":\"#odor$f#\",\"yName\":\"type\"}],\"12\": [{ \"xAxis\": \"p\", \"yAxis\":70.3921568627451},{ \"xAxis\": \"e\", \"yAxis\":29.607843137254903},{\"childrenIndex\":[], \"populationSize\":2040, \"filter\":\"#cap_shape$x#bruises$f#\",\"yName\":\"type\"}],\"13\": [{ \"xAxis\": \"p\", \"yAxis\":71.0376282782212},{ \"xAxis\": \"e\", \"yAxis\":28.96237172177879},{\"childrenIndex\":[], \"populationSize\":1754, \"filter\":\"#cap_shape$f#bruises$f#\",\"yName\":\"type\"}],\"14\": [{ \"xAxis\": \"p\", \"yAxis\":95.15738498789347},{ \"xAxis\": \"e\", \"yAxis\":4.842615012106537},{\"childrenIndex\":[], \"populationSize\":1652, \"filter\":\"#cap_surface$y#bruises$f#\",\"yName\":\"type\"}],\"15\": [{ \"xAxis\": \"p\", \"yAxis\":56.872037914691944},{ \"xAxis\": \"e\", \"yAxis\":43.127962085308056},{\"childrenIndex\":[], \"populationSize\":1688, \"filter\":\"#cap_surface$s#bruises$f#\",\"yName\":\"type\"}],\"16\": [{ \"xAxis\": \"p\", \"yAxis\":3.937007874015748},{ \"xAxis\": \"e\", \"yAxis\":96.06299212598425},{\"childrenIndex\":[], \"populationSize\":2032, \"filter\":\"#bruises$t#odor$n#\",\"yName\":\"type\"}],\"17\": [{ \"xAxis\": \"p\", \"yAxis\":100.0},{ \"xAxis\": \"e\", \"yAxis\":0.0},{\"childrenIndex\":[], \"populationSize\":1872, \"filter\":\"#bruises$f#odor$f#\",\"yName\":\"type\"}],\"18\": [{ \"xAxis\": \"p\", \"yAxis\":3.937007874015748},{ \"xAxis\": \"e\", \"yAxis\":96.06299212598425},{\"childrenIndex\":[], \"populationSize\":2032, \"filter\":\"#bruises$t#odor$n#\",\"yName\":\"type\"}],\"19\": [{ \"xAxis\": \"p\", \"yAxis\":100.0},{ \"xAxis\": \"e\", \"yAxis\":0.0},{\"childrenIndex\":[], \"populationSize\":1872, \"filter\":\"#bruises$f#odor$f#\",\"yName\":\"type\"}]}')
    var title = [];
    var idx = 0
    Object.keys(Dic).forEach(function(key){

        title[idx] = Dic[idx][Dic[idx].length-1]["filter"]
            if (idx==0){
                    title[idx] = "root";
                }
                else{
                    for(var j = 0; j<title[idx].length;j++){
                        if(title[idx][j] == "#"){
                            title[idx] = title[idx].substr(0, j) + '\n' + title[idx].substr(j + 1);
                        }
                        else if(title[idx][j] == "$"){
                            title[idx] = title[idx].substr(0, j) + '=' + title[idx].substr(j + 1);
                        }
                    }
                }

         idx+=1;
    })

    var i = cell.id .slice(1);
    tableChecked[i] = (tableChecked[i]+1)%3;
    if(tableChecked[i]==0)
            tableChecked[i] = 3;
        var color;
        if(tableChecked[i]==1)
            color = '#d9d9d9';
        else if(tableChecked[i]==2)
            color = '#ff9999';
        else if(tableChecked[i]==3)
            color =  '#bbff99';
    var tdid = 'td' + i.toString();
    document.getElementById(tdid).style.backgroundColor = color
    document.getElementById('selected').innerHTML=""
    document.getElementById('notselected').innerHTML=""

    Object.keys(tableChecked).forEach(function(key) {

        if(tableChecked[key]==3){

            document.getElementById('selected').innerHTML+=  key +":  "+ title[key] + "<br />";
        }
        else if(tableChecked[key]==2){
            document.getElementById('notselected').innerHTML+=  key +":  "+ title[key] + "<br />";
        }
    });



    
}


