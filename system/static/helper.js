function constructQuery(){
    query = {
        "dataset": $("#all_tables").val() || "",
        "xAxis": $("#xaxis").val() || "",
        "yAxis": $("#yaxis").val() || "",
        "aggFunc": $("input[name='aggFunc']:checked").val() || "",
        "filters": JSON.stringify(filters) ,
        "method": "query"
    }
    return query
}
function constructQueryCallback(){
    var query = constructQuery();
    $.post('/postQuery', query ,'application/json')
    .success(
        function(data){
            // results = JSON.parse(data);
            console.log(data)
            // Do stuff 
    })
}

function populateOptions(list,selector)
{
    console.log(selector.length)
    selector.innerHTML="";
    selector.options[0] = new Option("None","None");
    for (var i=0;i<list.length;i++){
      selector.options[i+1] = new Option(list[i],list[i]);
    }
    console.log("added options")
}

var columns =[];
$("#all_tables").change(function (){
    $.post('/getColumns',{
        "tablename": $("#all_tables").val()
    },'application/json').success( function(data){
        // console.log(data)
        columns = JSON.parse(data);
        console.log(columns)
        populateOptions(columns,document.getElementById("xaxis"));
        populateOptions(columns,document.getElementById("yaxis"));
        constructQueryCallback()
    })

})
$("#xaxis").change(constructQueryCallback)
$("#yaxis").change(constructQueryCallback)
$("input[name='aggFunc']").change(constructQueryCallback)
