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

// Attempting normalization by percentage
// var vspec =
//     {
// 		"$schema" : "https://vega.github.io/schema/vega-lite/v2.json",
//         "data" : {"values" :data },
//         "transform": [{
//             "window": [{
//                 "op": "sum",
//                 "field": "NumRecord",
//                 "as": "TotalRecords"
//             }],
//             "frame": [null, null]
//           },
//           {
//             "calculate": "datum.NumRecord/datum.TotalRecords * 100",
//             "as": "PercentOfTotal"
//           }],
//         "layer": [
//         	{"mark":"bar",
//         	 "encoding": {
//                 "x":{
//                     "field": "type",
//                     "type": "nominal"
//                 },
//                 "y":{
//                     "field": "PercentOfTotal",
//                     "type": "quantitative"
//                 }
//               }
//     		}
// 		]
// 	}
var vspec =
    {
		"$schema" : "https://vega.github.io/schema/vega-lite/v2.json",
        "data" : {"values" :data },
        "layer": [
        	{"mark":"bar",
        	 "encoding": {
                "x":{
                     "aggregate": "count",
                     "field": "Number of Records",
                     "type": "quantitative"
                },
                "y":{
                    "field": "type",
                    "type": "nominal"
                }
              }
    		}
		]
	}
vegaEmbed("#testContainer",vspec,{"actions" : false})
