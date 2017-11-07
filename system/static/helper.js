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
    console.log(fname)
    // Data Upload after options selection
    console.log(window.location.pathname)
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
    div.innerHTML += "<a href=\""+json_pathloc+"\">"+"filename:"+fname+"</a>"
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
$("#all_tables").change(function (){
    $.post('/getAvailableFiles',{
        "tablename": $("#all_tables").val()
    },'application/json').success( function(data){
        console.log(data)
        populateOptions(data["xAxis"],document.getElementById("xaxis"));
        populateOptions(data["dist"],document.getElementById("metric"));
        populateOptions(data["algo"],document.getElementById("algorithm"));
        populateOptions(data["ic"],document.getElementById("ic"));
        populateOptions(data["ip"],document.getElementById("ip"));
        populateOptions(data["k"],document.getElementById("k"));
    })
})
$("#submit").click(constructQuery)
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
function IsJsonString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}
// Direct input graphDic submission form 
$("#graphDicSubmit").click(function(){
    getNodeEdgeListThenDraw($("#graphDic").val());
})
// Trump Clinton example (display by default, on startup)
getNodeEdgeListThenDraw({'1': [{'xAxis': 'Clinton', 'yAxis': 48}, {'xAxis': 'Trump', 'yAxis': 46}, {'xAxis': 'Others', 'yAxis': 6}, {'filter': 'All', 'childrenIndex': [2, 3], 'yName': '% of vote'}], '2': [{'xAxis': 'Clinton', 'yAxis': 31}, {'xAxis': 'Trump', 'yAxis': 62}, {'xAxis': 'Others', 'yAxis': 7}, {'filter': 'Race = White', 'childrenIndex': [4, 5], 'yName': '% of vote'}], '3': [{'xAxis': 'Clinton', 'yAxis': 21}, {'xAxis': 'Trump', 'yAxis': 70}, {'xAxis': 'Others', 'yAxis': 9}, {'filter': 'Gender = F', 'childrenIndex': [], 'yName': '% of vote'}], '4': [{'xAxis': 'Clinton', 'yAxis': 21}, {'xAxis': 'Trump', 'yAxis': 52}, {'xAxis': 'Others', 'yAxis': 27}, {'filter': 'Color = Blue', 'childrenIndex': [5], 'yName': '% of vote'}], '5': [{'xAxis': 'Clinton', 'yAxis': 20}, {'xAxis': 'Trump', 'yAxis': 30}, {'xAxis': 'Others', 'yAxis': 50}, {'filter': 'Job = Student', 'childrenIndex': [], 'yName': '% of vote'}]})