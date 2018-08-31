var json = {
    questions: [
        {
            name: "name",
            type: "text",
            title: "Please enter your name:",
            placeHolder: "Jon Snow",
            isRequired: true
        }, {
            name: "email",
            type: "text",
            inputType: "email",
            title: "Your e-mail:",
            placeHolder: "jon.snow@nightwatch.org",
            isRequired: true,
            validators: [
                {
                    type: "email"
                }
            ]
        }
    ]
};

window.survey = new Survey.Model(json);

survey.onComplete
    .add(function (result) {
        document
            .querySelector('#q1')
            .innerHTML = "result: " + JSON.stringify(result.data);
    });

$("#q1").Survey({model: survey});

var json = {
    questions: [
        {
            type: "radiogroup",
            name: "expectation",
            title: "If you are asked to predict the distribution of mushroom type for “Cap color= c; Gill Spacing = c”, which one of these five visualization would you chose?",
            isRequired: true,
            colCount: 1,
            choices: [
                "Overall Distribution",
                "Cap color = b & Gill Spacing = c",
                "Cap Color = c &  Gill Spacing = c &  Cap Surface = y",
                "Gill Spacing = c / Cap Color = c"
            ]
        }
    ]
};
var xObj = {
    "field": "PercentOfTotal",
    "type": "quantitative",
    "axis": {"title":"Percent of Total"}
}
var vspec =
    {
		"$schema" : "https://vega.github.io/schema/vega-lite/v4.json",
        "data" : {"values" :data },
        "transform": [
           {
              "aggregate": [{
               "op": "sum",
               "field": "NumRecord",
               "as": "NRecord"
              }],
			  "groupby":["type"]
           },
		   {
            "window": [{
                "op": "sum",
                "field": "NRecord",
                "as": "TotalRecords"
            }],
            "frame": [null, null]
          },
          {
            "calculate": "datum.NRecord/datum.TotalRecords * 100",
            "as": "PercentOfTotal"
          }],
        "layer": [
        	{"mark":"bar",
        	 "encoding": {
                "y":{
                    "field": "type",
                    "type": "nominal"
                },
                "x":xObj
              }
          },{
            "mark": {
              "type": "text",
              "align": "right",
              "baseline": "middle",
              "dx": -25
            },
            "encoding": {
			  "y":{
                    "field": "type",
                    "type": "nominal"
                },
              "text": {
						"field": "PercentOfTotal",
                        "type": "quantitative",
                        "format": ".2f"
				},
			  "color":{"value":"white"}
            }
         }
		]
	}
vegaEmbed("#testContainer",vspec,{"actions" : false})
