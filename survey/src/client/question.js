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
// If you are asked to predict the distribution of mushroom type for “Cap color= c; Gill Spacing = c”, which one of these five visualization would you chose?
// Overall Distribution
// Cap color = b & Gill Spacing = c
// Cap Color = c &  Gill Spacing = c &  Cap Surface = y
// Gill Spacing = c / Cap Color = c
