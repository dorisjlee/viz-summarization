var columns =[];
$("#all_tables").change(function ()
    {console.log("test")
    // $.post('/getColumns',{
    //     "table_name": $("#all_tables").val()
    // }).success( function(data){
    //     // console.log(data)
    //     columns = JSON.parse(data);
    //     console.log(columns)
    //     populateOptions(columns,document.getElementById("xaxis"));
    //     populateOptions(columns,document.getElementById("yaxis"));
    // })
})

function populateOptions(list,selector)
{
    console.log(selector.length)
    for (var i=0;i<list.length;i++){
      selector.options[i+1] = new Option(list[i],list[i]);
    }
    console.log("added options")
}