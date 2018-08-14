/**
 * @fileoverview main.js is the javascript main entry point for the app.
 */
/*jslint nomen: true    */
/*jslint plusplus: true */
/*jslint todo: true     */
/*jslint browser: true  */
'use strict';

/**
 *
 * @constructor
 */
vizta.Main = function () {
  // connect up the menu's onClick once the DOM has loaded
  // state manager
  this.eventMgr = new vizta.EventMgr();
  this.stateMgr = new vizta.StateMgr(this.eventMgr);
  this.feedbackMgr = new vizta.FeedbackMgr();
  this.showMe = new vizta.ShowMe();
  this.inputTextDefaultText = 'Input Query Here';//'type or say "Find large earthquakes near California"';
  this.RETURN_KEY = 13; // for the keydown event in inputText
  this.SPACE_BAR = 32;
  this.termList = [];  // the tokens from the input
  this.wordList = [];  // [id, tag, value] tuples from nlp parsing opt=sentence
  this.wordTree = {};  // results of nlp parsing opt=tree
  this.place = '';
  this.size = '';
  this.min = 1;
  this.max = 10;
  this.minLargeMag = 5;
  this.dataattribute = '';
  this.rp1;
  this.rp2;

  this.month = '';
  this.valueTypes = [];
  // this.viz = 'map';
  // this.datasource = 'usgs_earthquakes';
  this.viz = 'dashboard';
  this.datasource = 'titanic';
  this.timeattribute = '';

  // handle whether we are on a touch device or using a mouse
  var clickEvent = 'ontouchstart' in window ? 'touchstart' : 'click';

  // show the version number defined in vizta.js
//  this.versionEle = document.querySelector('.titleVersion');
//  this.versionEle.innerHTML = ['v', vizta.VERSION].join('');
  this.slider;
  this.sliderBubble;

  // add an event handler for the debug button to toggle showing debug info

  // get a reference to the text box
  this.inputText = document.querySelector('.inputText');
  this.inputText.value = this.inputTextDefaultText;
  this.inputText.addEventListener('focus', this.handleInputText.bind(this));
  this.inputText.addEventListener('keyup', this.handleInputText.bind(this));
  this.inputText.addEventListener('change', this.handleInputText.bind(this));

  // add an event handler to parse the spoken string
  this.parseButton = document.querySelector('.parseButton');
  this.parseButton.addEventListener(clickEvent,
    this.handleParseButton.bind(this));

  // get a reference for the outputContainer
  this.outputContainer = document.querySelector('.outputContainer');

  // get a reference for the titleContainer
  this.charttitleContainer = document.querySelector('.charttitleContainer');

  // get a reference for the errorContainer
  this.errorContainer = document.querySelector('.errorContainer');


  // get a reference for the vizContainer
  this.vizContainer = document.querySelector('.mainVizContainer');

  //get a reference for the commandContainer
  this.commandContainer = document.querySelector('.commandContainer');

  this.menu = new vizta.Menu();
  // console.log("before initAutocomplete")
  this.initAutocomplete();
  // console.log("after initAutocomplete")
  this.initVizType = 'dashboard';
  vizta.Menu.prototype.populateDataDropDown(['outbreaks', 'titanic','HPI','housing']);
  // console.log("end Main()")
};

var Context = function(params) {
  // Maintains some context of what is happening (required for autocomplete module to work correctly)
  this.dataset = params.dataset;
  this.datasetName = params.datasetName;
  this.chart = params.chart;
  this.startRule = params.startRule;
  this.domains = params.domains;
};
vizta.Main.prototype.initAutocomplete = function () {
  var dataDir = '../data';
  var parserDir = '../nlp/parser';
  var lexiconDir = parserDir + '/lexicons';
  var nlp = require('../nlp/nlp.js');

  // Parameter parsing
  var urlParams = nlp.Utils.getUrlParams();
  // Extract autocomplete settings from url params
  var autocompleteUrlParams = {};
  for (var k in urlParams) {
    if (k.startsWith('ac.')) {
      var name = k.substring(3);
      autocompleteUrlParams[name] = nlp.Utils.parseStr(urlParams[k]);
    }
  }
  // console.log(autocompleteUrlParams);
  // Default autocomplete parameters
  var inputElement = $(this.inputText);
  var autocompleteDefaults = {
    sampleSlots: false,           // Whether to sample slots
    highlightTypes: false,        // Highlight certain slots by color
    useSemanticGrouping: false,   // Group by semantic types
    useTwoStage: false,           // whether to use two stage autocomplete or not
    longSuggestionsCap: 0,        // offer long suggestions if there are less than x options (set to 0 not to have long suggestions)
    preQuery: false,              // whether to perform querying and filtering of the data as we go, and indicate using previz or some textural hint, the amount of data available
    maxItems: 10,                 // Maximum number of items to display in the list
    useFuzzy: true,               // whether to use fuzzy matcher
    autoSelect: false,            // whether to automatically select the first option
    useSuggestInput: false,       // whether to show auto selected template in gray in the background
    filterAliases: true,          // filter aliases
    // Widget options
    useWidgets: false,             // whether to use widgets
    useCalendar: false,           // whether to use the calendar widget or not (will be replaced by 'slider' or 'histogram' if false)
    showTextList: 'auto',         // whether to show list of suggests along with widgets (options: 'auto' - show when widgets not visible, 'never', 'always')
    expandWidgets: 'auto',        // when to show/expand widgets (options: 'auto' - automatically expand widget, 'click' - expand widget on click)
    widgetPosition: 'queryEnd'    // where to position the widgets (options: inline, queryEnd, float)
  };
  var autocompleteParams = $.extend({}, autocompleteDefaults, autocompleteUrlParams);
  autocompleteParams = $.extend(autocompleteParams, {
    input: inputElement,         // input element
    fuzzyMatcher: autocompleteParams.useFuzzy? fuzzy : undefined
  });

  var useWidgets = autocompleteParams['useWidgets'];
  var Autocomplete = useWidgets? nlp.AutocompleteWithWidgets : nlp.Autocomplete;
  var SemanticFns = nlp.SemanticFns;
  var corenlp = new nlp.CoreNLP();
  var attributeMatcher = new nlp.AttributeMatcher();
  this.attributeMatcher =  attributeMatcher;
  var templates = new nlp.TemplateIndex();
  templates.load(parserDir + '/grammars/autocomplete.json');

  // Set up autocomplete
  var autocomplete = new Autocomplete(autocompleteParams);

  var parser = new nlp.Parser({ autocomplete: autocomplete , corenlp: corenlp, attributeMatcher: attributeMatcher });
  this.parserObj = parser;
  // Initialize lexicon
  var lexicons = new nlp.Lexicons({
    dataDir: dataDir,
    lexiconDir: lexiconDir,
    useDefaultCountries: false,
    useDefaultStates: true,
    useDefaultCities: false
  });
  lexicons.bind('update', parser.onLexiconUpdated.bind(parser));
  lexicons.init();

  // Bind domain specific interpretation of values
  lexicons.addDomain(
    {
      name: 'geo',
      grammarMappings: [
        {
          grammarType: 'size',
          values: {
            'small':    new SemanticFns.FilterSizeFn({ selectFn: 'select_by_Qattribute', min: SemanticFns.min, max: SemanticFns.mean }),
            'large':    new SemanticFns.FilterSizeFn({ selectFn: 'select_by_Qattribute', min: SemanticFns.mean, max: SemanticFns.max }),
            'largest':  new SemanticFns.FilterSizeFn({ selectFn: 'select_top_n', value: 10 }),
            'smallest': new SemanticFns.FilterSizeFn({ selectFn: 'select_bottom_n', value: 10 })
          }
        }
      ]
    }
  );
  // TODO: load only once - hookup with actual data load and datamgr
  console.log();
  // console.log("before _loadAndLinkDatasets")
  lexicons._loadAndLinkDatasets(lexiconDir + "/datasets.json");//,function(){main.initViz()});
  // console.log("after _loadAndLinkDatasets:",lexicons.datasets)

  // console.log("main.getCurrentDataset():",main.getCurrentDataset())
  // Set up parse visualization
  var parseGraphElement = $('#parseGraph');
  var parseViewer = new nlp.ParseViewer({
    selector: '#parseGraph'
  });
  var showParseElement = $('#showparse');
  var showParse = showParseElement.prop('checked');
  showParseElement.change( function() {
    showParse = $(this).prop('checked');
    if (showParse) {
      // Force parse and show element
      parseGraphElement.show();
      parseInput();
    }
    else { parseGraphElement.hide(); }
  });

  // Hookup buttons - hookup on keyup so that the input is updated
  inputElement.keyup( function(event) {
    if (isAutocompleteEnabled()) {
      updateSuggestions();
    }
  });
  inputElement.keydown( function(event) {
    // Tab = 9 - force suggestion
    // Down arrow = 40
    if (event.keyCode === 40 || event.keyCode === 9) {
      if (isAutocompleteEnabled()) {
        updateSuggestions(true);
        autocomplete.showPopup();
      }
    }

    if (event.keyCode === 9) {
      return false;
    }
  });
  // Hookup parsing without autocomplete when the parse button is pressed
  this.parseButton.addEventListener('click', parseInput);
  var autocompleteEnabledCheckbox = $('#autocomplete');
  function isAutocompleteEnabled() {
    return autocompleteEnabledCheckbox.prop('checked');
  }
  function parseInput() {
    var text = inputElement.val();
    // Track original input since that maybe normalized before parsing
    var parseOptions = { originalInput: text, skipAutocomplete:true };

    parse(text, parseOptions);
  }
  function updateSuggestions(suggestMore) {
    var text = inputElement.val();
    // Track original input since that maybe normalized before parsing
    var parseOptions = { originalInput: text, suggestMore: suggestMore };
    var hasSuggestions = autocomplete.hasSuggestions();
    if (autocomplete.processedText !== text || (!hasSuggestions && parseOptions.suggestMore)) {
      //console.log('parsing ' + text);
      //console.log('old processed ' + autocomplete.processedText);
      parse(text, parseOptions);
    }
  }

  // Main parse function
  function parse(input, parseOptions) {
    input = lexicons.stripDiacritics(input).toLowerCase();

    // TODO: only set context when data or chart changes
    updateContext();
    parser.parse(input, parseOptions);
  }

  var scope = this;
  function updateContext() {
    // Context of a viz is determined by the chartType and the dataset
    var chartType = scope.viz;
    // Assumes that datasource is set correctly....
    var datasetName = scope.datasource;
    var dataset = lexicons.getDataset(datasetName);
    // console.log("updateContext dataset:",dataset)
    main.currentDataset = dataset;



    var domains = (dataset) ? dataset.domains : ['geo', 'temporal'];
    var startRule;

    // Set filtering of autocomplete based on grammar rules
    var hasLocation =  chartType === 'map' || domains.indexOf('geo') >= 0;
    var hasTemporal = domains.indexOf('temporal') >= 0;

    // default params
    var options = {
      allowLocation: hasLocation,  // Top level switch (must be specified)
      allowState: false,
      allowCountry: false,
      allowCity: false,

      allowTemporal: hasTemporal,  // Top level switch (must be specified)
      allowMonth: false,

      allowSize: false      // Top level switch (must be specified)
    };

    var commonShowVerbs = ['show', 'show me', 'find',  'highlight'];
    var domainShowVerbs = {
      'geo': ['where', 'where are', 'where do I see'],
      'temporal': ['when'],
      'monetary': ['how much']
    };
    var chartShowVerbs = {
      'map': ['where', 'where are', 'where do I see']
    };

    // Let adjust our options based on the chartType and the current selected data
    // For now assume chartType correspond to just one data
    switch(chartType) {
      case 'map':
        //options.allowTemporal = false;
        options.allowMonth = true;
        options.allowTemporalAdj = true;
        options.allowState = true;
        options.allowSize = true;
        options.allowSeason = false;
        lexicons.setLexicon('size', ['smallest', 'small', 'large', 'largest', 'big', 'biggest']);
        startRule = 'mapCmd';
        break;
      case 'line':
        //options.allowTemporal = true;
        options.allowTemporalAdj = false;
        options.allowMonth = true;
        options.allowSize = true;
        lexicons.setLexicon('size', ['highest', 'lowest', 'maximum', 'minimum', 'average']);
        startRule = 'lineCmd';
        break;
      case 'scatter':
        //options.allowTemporal = true;
        options.allowTemporalAdj = false;
        options.allowCountry = true;
        options.allowSeason = false;
        options.allowSize = true;
        startRule = 'scatterCmd';
        lexicons.setLexicon('size', ['highest', 'lowest', 'maximum', 'minimum', 'smallest', 'largest', 'biggest']);
        break;
      case 'bar':
        startRule = 'barCmd';
        options.allowCountry = true;
        options.allowSize = true;
        lexicons.setLexicon('size', ['highest', 'lowest', 'maximum', 'minimum', 'smallest', 'largest', 'biggest', 'big', 'small', 'large', 'high', 'low']);
        break;
      case 'dashboard':
        startRule = 'dashCmd';
        options.allowSize = true;
        options.allowCity = true;
        break;
    }

    var allowedShowVerbs = commonShowVerbs;
    for (var i = 0; i < domains.length; i++) {
      var vs = domainShowVerbs[domains[i].name];
      if (vs) {
        allowedShowVerbs = allowedShowVerbs.concat(vs);
      }
    }
    var vs = chartShowVerbs[chartType];
    if (vs) {
      allowedShowVerbs = allowedShowVerbs.concat(vs);
    }

    var skipShowVerbs = ['show me', 'where', 'where do I see', 'how about'];
    lexicons.setLexicon('show', allowedShowVerbs.filter( function(x) { return skipShowVerbs.indexOf(x) < 0; }));
    lexicons.addToLexicon('show', allowedShowVerbs.filter( function(x) { return skipShowVerbs.indexOf(x) >= 0; }), { isAlias: true } );
    autocomplete.filterTemplateSlotOptions = function(slot, choices) {
      if (slot.name === 'show') {
        choices = choices.filter(function (x) {
          return skipShowVerbs.indexOf(x) < 0;
        });
      } else if (slot.name === 'attribute') {
        if (slot.parentTemplate.name === 'selectRangeCmd' || slot.parentTemplate.name === 'attributeFilterRange') {
          choices = choices.filter(function (x) {
            var entries = x.entries || lexicons.lookupEntries(x.term || x);
            //var ok = !entries || entries[0].value.dataType === 'numeric' || entries[0].value.dataType === 'Date';
            var ok = !entries || (entries[0].value.dataType !== 'categorical' && entries[0].value.dataType !== 'string');
            return ok;
          });
        }

      }
      return choices;
    };

    // Set the parser context based on the viz
    var oldContext = parser.context;
    parser.context = new Context({
      dataset: dataset,
      datasetName: datasetName,
      domains: domains,
      chart: scope.viz,
      startRule: startRule
    });
    // check if context changed
    var contextChanged = !oldContext || oldContext.datasetName !== parser.context.datasetName || oldContext.chart !== parser.context.chart;
    if (contextChanged) {
      // reset our autocomplete
      autocomplete.reset();
    }

    autocomplete.context = parser.context;

    lexicons.filterEntries = function(x) {
      //console.log('filterEntries');
      //console.log(x.dataset);
      if (x.suggest != undefined && !x.suggest) return false;
      var datasetOk = (x.dataset != undefined)? x.dataset === parser.context.datasetName : false;
      var chartOk = (x.chart != undefined)? x.chart === parser.context.chart : false;
      return datasetOk || chartOk || (x.dataset == undefined && x.chart == undefined);
    };
    // Set what we will show for the autocomplete
    autocomplete.setFilterRules( parser.getFilterRules(options) );
    autocomplete.setTemplates( templates, scope.viz );

    // console.log("updateContext:")
    // console.log("scope:",scope)
    // console.log("parser.context:",parser.context)
    // if (main!= null){
    //   mapContext2Spec(main.visMgr);
    // }
  }

  parser.bind('parseSuccess', this.onParseSuccessful.bind(this));
  parser.bind('parseUnsuccess',this.onParseUnsuccessful.bind(this));
  parser.bind('parseFinished', function (tree) {
    if (showParse) {
      parseViewer.showParse(tree);
    }
  });

  this.parser = {
    updateSuggestions: updateSuggestions,
    parse: parse,
    updateContext: updateContext
  }
  parser.bind(this.onParseSuccessful.bind(this));
};

vizta.Main.prototype.onParseUnsuccessful = function (tree) {
  var errorTerms = '';
  if(main.runAutoCorrectQuery) {
    if(main.parserObj.errorTerms){
      errorTerms = main.parserObj.errorTerms.join(' ');
    }

    main.errorContainer.innerHTML =  "The system doesn't understand the query: "+'<b><i>'+errorTerms+'</i></b>';
    var validChild=null;
    for (var i=0;i<tree.children.length;i++){
      if(tree.children[i].constructor.name.indexOf('CmdContext')!=-1){
        validChild = tree.children[i];break;
      }
    }
    if(validChild!=null)
    if(validChild.value!=""){
      this.dashboardController.currentQuery = validChild.value;
      this.dashboardController.currentQueryType = validChild.constructor.name;
      this.dashboardController.queryWidgets = validChild.queryWidgets;
      var suggestedText =       ' <a style="color:#1a0dab" href="" > Showing results for ';
      main.errorTextAppendMode =true;
      for(var i=0;i<main.termList.length;i++){
        if(errorTerms.indexOf(main.termList[i])===-1){
          suggestedText = suggestedText+' '+main.termList[i];
        }
      }
      suggestedText = suggestedText+'</a>'
      //main.errorContainer.innerHTML = main.errorContainer.innerHTML+suggestedText;
      main.errorTextAppend = "<font color='red'>The system doesn't understand the query: "+'<b><i>'+errorTerms+'</i></b></font>'+suggestedText+'. ';
      //setTimeout(function(){
      main.dashboardController.updateDashboard(main.termList, true);
      //}, 50);

    }


  }

};
vizta.Main.prototype.onParseSuccessful = function (tree) {
  if(tree.children[0].value!=""){
    this.dashboardController.currentQuery = tree.children[0].value;
    this.dashboardController.currentQueryType = tree.children[0].constructor.name;
    this.dashboardController.queryWidgets = tree.children[0].queryWidgets;
  }

  if(main.runAutoCorrectQuery){
    main.dashboardController.updateDashboard(this.termList, true);
    if(this.suggestedTerms.join('')!=this.termList.join('')){
      var suggestedText = 'Showing results for <a style="color:#1a0dab" href="" '+'onclick="runCorrectedQuery(); return false;">';
      for(var i=0; i<this.suggestedTerms.length;i++){
        if(this.suggestedTerms[i] === this.termList[i]){
          suggestedText+=this.suggestedTerms[i]+' ';
        }
        else {
          suggestedText+='<b><i>'+this.suggestedTerms[i] +'</i></b>'+' ';
        }
      }

      suggestedText+='</a>';
      main.errorContainer.innerHTML =   '<span style="font-size: 18px;color:#222" '+'>'+suggestedText +'</span>';
    }
  }
};
// vizta.Main.prototype.testFactorRecommend = function(e) {
//   var factorGraph = new vizta.FactorGraph();
//   factorGraph.computePivotFactorGraph()
//   factorGraph.getTopKRecommendationsFromPivotFactorGraph()
//   var factorGraph = new vizta.FactorGraph();
//   factorGraph.computeElaborateFactorGraph()
//   factorGraph.getTopKRecommendationsFromElaborateFactorGraph()
// }
/**
 * Function for initializing viz based on viz type enabled.
 */
vizta.Main.prototype.initViz = function(e) {
  var ambiguityWidgetDashboard = document.querySelector('#AmbiguityWidgets');
  if(ambiguityWidgetDashboard){
    ambiguityWidgetDashboard.innerHTML = "";
  }
  var visContainer = document.querySelector('#mainVizContainer');
  var errorMsg = '';
  this.errorContainer.innerHTML = errorMsg;

  if (this.datasource == 'housing') {
      this.inputTextDefaultText = 'type "Show me condos with price less than 200000"'
      this.inputText.value =  this.inputTextDefaultText;
      this.charttitleContainer.innerHTML = '<center><h3> Past Home Sales - Seattle </h3></center>';
   	  var dataSource = '../data/seattle_housing_sales_redfin_neighborhoods.csv';
   	  this.timeattribute = 'last_sale_date';
      this.dataMgr = new vizta.DataMgr(dataSource, this.initDashboard.bind(this));

      this.initDashboardCommandsCard('<img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Home Type <br /> <img src="../assets/schemaicons/SchemaViewer_Date_Continous.png" /> Last_Sale_Date <br /> <img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Zillow Neighborhood <br />', '<img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Last_Sale_Price <br /> <img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> Latitude <br /> <img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> Longitude <br /> <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Sqft');
  }else if (this.datasource == 'outbreaks') {
   	this.inputTextDefaultText = 'type "measles in India"';
		this.inputText.value = this.inputTextDefaultText;
    this.charttitleContainer.innerHTML = '<center><h3> Disease Outbreaks Around the World</h3></center>';
    var dataSource = '../data/outbreaks.csv';
    this.timeattribute = 'Year';
    this.dataMgr = new vizta.DataMgr(dataSource, this.initDashboard.bind(this));
    this.initDashboardCommandsCard('<img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Country <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Continent <br /> \
                                    <img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Impact <br /> \
                                    <img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Disease <br /> \
                                    <img src="../assets/schemaicons/SchemaViewer_Date_Continous.png" /> Year',
                                    '<img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Cases <br />\
                                     <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Fatalities <br/>\
                                     <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Number of Records <br/>\
                                     <img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> Latitude <br />\
                                     <img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> Longitude');
  }else if (this.datasource == 'HPI') {
   	this.inputTextDefaultText = 'type "HappyPlanetIndex by WellBeing"';
		this.inputText.value = this.inputTextDefaultText;
    this.charttitleContainer.innerHTML = '<center><h3> Happy Planet Index Around the World</h3></center>';
    var dataSource = '../data/HPI2016fullCleanedEnriched.csv';
    // this.timeattribute = 'Year';
    this.dataMgr = new vizta.DataMgr(dataSource, this.initDashboard.bind(this));
    this.initDashboardCommandsCard('<img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> Country <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> SubRegion <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> region <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> status <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> currency <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> longitude <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> latitude <br/> '
                                    ,
                                    '<img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> HPIRank <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> LifeExpectancy <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> WellBeing <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> HappyLifeYears <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Footprint <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> HappyPlanetIndex <br/>\
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> InequalityOfOutcomes <br/>\
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> InequalityAdjustedLifeExpectancy <br/>\
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> InequalityAdjustedWellbeing <br/>\
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Population <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> GDPPerCapita <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> landlocked <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> NumberOfOfficialLanguages <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> NumberOfBorderingCountries <br/>\
                                    <img src="../assets/schemaicons/SchemaViewer_Geo_Continuous.png" /> area <br/> \
                                    ');
  }else if (this.datasource == 'titanic') {
   	this.inputTextDefaultText = 'type "survival by gender"';
		this.inputText.value = this.inputTextDefaultText;
    this.charttitleContainer.innerHTML = '<center><h3> Titanic Passenger Survival Records</h3></center>';
    var dataSource = '../data/titanic.csv';
    // this.timeattribute = 'Year';
    this.dataMgr = new vizta.DataMgr(dataSource, this.initDashboard.bind(this));
    this.initDashboardCommandsCard('<img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Class <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Sex<br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Survived?<br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Age (bin)<br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Fare (bin)<br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Siblings & Spouses Aboard?<br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Text_Discrete.png" /> Children Aboard?',
                                    '<img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> NumberSurvived <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> %survived <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> AverageAge <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Number of Records <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Age <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Fare <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Parch <br/> \
                                    <img src="../assets/schemaicons/SchemaViewer_Number_Continuous.png" /> Sibsp'
                                  );
  }

  if (this.parser) {
    this.parser.updateContext();
  }
};


vizta.Main.prototype.initDashboardCommandsCard = function(dims, measures) {

  // var commandCardInner = this.commandCard.getInnerContainer();
  // add some text to the command card
  // TODO replace this with the whoWhatWhenWhere object/array
  var outerContainer = document.createElement("div"); outerContainer.className="viztaCardOuterContainer";
  var innerContainer = document.createElement("div"); innerContainer.className="viztaCardInnerContainer";
  var ccardInnerHTML = document.createElement("div"); ccardInnerHTML.className="commandcardinnerhtml";
  innerContainer.appendChild(ccardInnerHTML)
  outerContainer.appendChild(innerContainer)
  $(".commandContainer")[0].appendChild(outerContainer)
  ccardInnerHTML.innerHTML = ['<font color = "black"><b>Dimensions</b></font> <br />' + dims, '<br>',
      '<font color = "black"><b>Measures</b></font> <br />' + measures, '<br>'
    ].join('');
}


/**
 * Once the csv file has loaded, this method is called to intialize the app.
 */
vizta.Main.prototype.initDashboard = function() {
  this.root = this.vizContainer;
  this.eventMgr = new vizta.EventMgr();

  this.commandContainer = document.querySelector('.commandContainer');


  this.createDashboard(this.root);



};




vizta.Main.prototype.createDashboard = function(parent) {
  showSpinner();
  this.showDebugFlag =false;
  this.dashboardController = new vizta.DashboardController(this);
  this.resetMgrs();
  this.debugContainer = document.querySelector('.debugContainer');
  this.debugContainer.innerHTML = '' +
    '<div class=AmbiguityWidgetContainer id=AmbiguityWidgets style="text-align: center";> ' +
    '</div>';
  this.outputContainer = document.querySelector('.outputContainer');
  this.outputContainer.innerHTML = '';

  this.tooltipDiv = d3.select("body").append("div")
    .attr("class", "charttooltip")
    .style("display", "none");

  var vizContainerWidth = 1400,  vizContainerHeight =1000;

  var main = this; // pass main to each card so they can get at main's vars
  var allData = main.dataMgr.getRows();

  // No initial visualization in dashboard
  if (this.datasource == 'housing') {
	  var mapChartOptions = {
		chartType: "mapChart",
		width: 1000, height: 400,
		position: 1,
		minZoom: 1,
		dataParams: {rawData: allData, dimension: ['latitude', 'longitude'], measure:'sqft'},
		encoding: {marks: {type:"circle", data: "table", property: [{size:'sqft'}]}},
		title: 'Map Chart',
		tooltips: [{attr: 'last_sale_price', display_attr: 'last sale price'},
      {attr: 'sqft', display_attr: 'sqft'},{attr: 'home_type', display_attr: 'home type'},
      {attr: 'beds', display_attr: 'bedrooms'},{attr: 'baths', display_attr: 'bathrooms'},
      {attr: 'last_sale_date', display_attr: 'Sale date'}]
	  };
	 this.initChart = new vizta.MapChart(mapChartOptions);
   this.mapChart = this.initChart
  }else if (this.datasource == 'outbreaks') {
    var mapChartOptions = {
  		chartType: "mapChart",
  		width: 1000, height: 400,
  		position: 1,
  		minZoom: 2,
  		dataParams: {rawData: allData, dimension: ['latitude', 'longitude'], measure:'Cases'},
  		encoding: {marks: {type:"circle", data: "table", property: [{size:'Cases'}]}},
  		title: 'Map Chart',
  		tooltips: [{attr: 'Disease', display_attr: 'Disease'}, {attr: 'Country', display_attr: 'Country'}, {attr: 'Cases', display_attr: 'Cases'}]
  	  };
    this.initChart = new vizta.MapChart(mapChartOptions);
    this.mapChart = this.initChart
  }else if (this.datasource == 'HPI') {
    var scatterPlotOptions = {
      chartType: "scatterPlot",
      width: 686,
      height: 330,
      position: main.visMgr.getVisCount() + 1,
      dataParams: {rawData: main.dataMgr.getRows(), dimension: "HappyLifeYears", measure: "AverageWellBeing"},
      title: "",
      labelXAxis: "HappyLifeYears",
      labelYAxis: "AverageWellBeing"
    };
    this.initChart = new vizta.ScatterPlot(scatterPlotOptions);
  }else if (this.datasource == 'titanic') {
    var barChartOptions = {
      chartType: "barChart",
      width: 686,height: 330, position: main.visMgr.getVisCount()+1,
        dataParams: {rawData: main.dataMgr.getRows(), dimension: "Sex" , measure:"Number of Records", rollup:"sum",horizontalBar:true},
      title: "", labelXAxis: "Sex", labelYAxis: "Sum of Number of Records"
    };
    this.initChart = new vizta.BarChart(barChartOptions);
  }
  this.visMgr.addVis(this.initChart);
  var interactionState = new InteractionState("init");
  interactionState.updateStateThenRecommend()
};
vizta.Main.prototype.resetMgrs = function(){
  //When we switch to a new dataset, we have to reinitalize all global managers including StateMgr, feedbackMgr, featureMgr; DataMgr (already updated),
  this.visMgr = new vizta.VisMgr();
  this.stateMgr = new vizta.StateMgr(this.eventMgr);
  this.feedbackMgr = new vizta.FeedbackMgr();
  this.featureMgr = new vizta.FeatureMgr(this.stateMgr);
}

/**
 * When the user hits enter, construct the image
 */
vizta.Main.prototype.handleInputText = function(e) {

  if(main.runAutoCorrectQuery){
    main.runAutoCorrectQuery =false;
  }
  var text = this.inputText.value;
  main.termList = [];
  if (text === this.inputTextDefaultText) {
    this.inputText.value = '';
    return;
  }

  text = text.trim();
  if (e.keyCode === this.RETURN_KEY && text.length > 0) {
    //var tterms = text.split(" ");
    var tterms = text.split(/[\s,?.;:!]+/);
    for (var i = 0; i < tterms.length; ++i) {
      if (tterms[i] != "") {
        main.termList.push(tterms[i]);
      }
    }

  if(main.changeQuerybyWidget){
    runParser( this.inputText.value, function() {
      setTimeout( function () {
        main.processDashboardNlpResults(function(){
          var AWState = new AmbiguityWidgetState();
          AWState.updateStateThenRecommend()
        });
        main.changeQuerybyWidget =false;
      }, 500);

    });
  }
  main.processDashboardNlpResults(function(){
    var utteranceState = new UtteranceState();
    utteranceState.updateStateThenRecommend()
  });



    //
  }
};


/**
 *
 */
vizta.Main.prototype.handleDebugButton = function(e) {
  this.showDebugFlag = !this.showDebugFlag;

  if (this.showDebugFlag) {
    this.debugButton.innerHTML = 'Hide debug';
    this.debugContainer.style.display = 'block';
  } else {
    this.debugButton.innerHTML = 'Show debug';
    this.debugContainer.style.display = 'none';
  }
};


/**
 *
 */
vizta.Main.prototype.handleParseButton = function(e) {
  if (this.inputText.value === this.inputTextDefaultText) {
    this.inputText.value = 'large earthquakes near California';
  }

  if (this.inputText.value && this.inputText.value.length > 0) {
    //this.parse(this.inputText.value);
    //var tterms = this.inputText.value.split(" ");
    var tterms = this.inputText.value.split(/[\s,?.;:!]+/);
    for (var i = 0; i < tterms.length; ++i) {
      if (tterms[i] != "") {
        main.termList.push(tterms[i]);
      }
    }
    main.processDashboardNlpResults(function(){
      var utteranceState = new UtteranceState();
      utteranceState.updateStateThenRecommend()
    });
  }
};


/**
 *
 */
vizta.Main.prototype.parse = function(terms) {
  var callback = 'handleNlpParseSentence';
  var url = ['http://nlp.naturalparsing.com/api/parse',
    '?format=json',
    '&jsoncallback=', encodeURIComponent(callback),
    '&input=', encodeURIComponent(terms),
    '&version=0.1',
    '&options=sentence'
  ].join('');

  // create a script node we will use to request a service to parse nlp
  this.nlpScriptEle = common.Dom.createScript(url, null);
};


/**
 * When the user clicks on the mute button, toggle the image.
 */
vizta.Main.prototype.handleMuteButton = function() {
  if (this.muteButton.className === 'unmuted') {
    this.muteButton.className = 'muted';
    this.mute = true;
    annyang.abort(); //pause

  } else {
    this.muteButton.className = 'unmuted';
    this.mute = false;
    annyang.start(); //resume
  }
};




/**
 *
 */
function handleNlpParseSentence (data) {
  common.Dom.deleteScript(main.nlpScriptEle);
  var wordCount = data.words.length;
  var msg = [];
  main.termList = [];
  main.wordList = [];  // results of nlp parsing opt=sentence
  main.wordTree = {};  // results of nlp parsing opt=tree
  msg.push(['There are ', wordCount, ' words:'].join(''));
  for (var i = 0; i < wordCount; ++i) {
    main.termList.push(data.words[i].value);
    main.wordList.push([data.words[i].id,
      data.words[i].tag,
      data.words[i].value]);
  }


  // now get the nlpParseTree
  var callback = 'handleNlpParseTree';
  var url = ['http://nlp.naturalparsing.com/api/parse',
    '?format=json',
    '&jsoncallback=', encodeURIComponent(callback),
    '&input=', encodeURIComponent(main.termList.join(' ')),
    '&version=0.1',
    '&options=tree'
  ].join('');
  main.nlpScriptEle = common.Dom.createScript(url, null);
};


/**
 *
 */
function handleNlpParseTree (data) {
  common.Dom.deleteScript(main.nlpScriptEle);
  var output = formatTree(data, 'tree data<br>\n', 0);

  main.wordTree = JSON.parse(JSON.stringify(data)); // fast copy

  main.processDashboardNlpResults(function(){
    var utteranceState = new UtteranceState();
    utteranceState.updateStateThenRecommend()
  });
}

// need to support all these cases
// show 'all', '2014'
// in, near
// average, highest, lowest
// specific dates
// seasons, semester, quarter, half-year
vizta.Main.prototype.processLineNlpResults = function() {
  // display termList. wordList, wordTree
  var wordCount = this.termList.length;
  var msg = [''];

  var errorMsg = '';
  main.errorContainer.innerHTML = errorMsg;

  var lineFn;


  if (this.datasource == 'temperatures') {
    lineFn = lineObj;

  }
  else if (this.datasource == 'stock prices') {
    lineFn = stocklineObj;

  }

  if (lineFn.maxStatus == false && lineFn.minStatus == false && lineFn.avgStatus == false) {
    this.valueTypes = [];
  }

  for (var i = 0; i < wordCount; ++i) {
    // this.termList[i] = this.termList[i].toLowerCase();
    switch(this.termList[i]) {
      case 'before':
        this.month = checkNearEntity(i, this.termList).toLowerCase();
        if (this.month == 'january')
          errorMsg = 'No data exists before January 2014';

        else if (lineFn.months.indexOf(this.month) > -1) {
          var mainMonthNumber = Date.getMonthNumberFromName(this.month);
          mainMonthDays = Date.getDaysInMonth(lineFn.year, mainMonthNumber);
          startDate = "01/" + mainMonthNumber  + "/" + lineFn.year;
          endDate =  mainMonthDays + "/" + mainMonthNumber + "/" + lineFn.year;
          lineFn.semanticZoom(startDate, endDate);


        }
        this.commandCard.updateWhenAttribute(startDate + ' - ' + endDate);
        msg.push('<br />before ' + this.month);
        break;
      case 'after':
        this.month = checkNearEntity(i, this.termList).toLowerCase();
        if (this.month == 'december')
          errorMsg = 'No data exists after December 2014';

        else if (lineFn.months.indexOf(this.month) > -1) {
          var mainMonthNumber = Date.getMonthNumberFromName(this.month);
          mainMonthNumber = mainMonthNumber + 2;
          mainMonthDays = Date.getDaysInMonth(lineFn.year, mainMonthNumber);
          startDate = "01/" + mainMonthNumber  + "/" + lineFn.year;
          endDate =  mainMonthDays + "/" + mainMonthNumber + "/" + lineFn.year;
          lineFn.semanticZoom(startDate, endDate);
        }
        this.commandCard.updateWhenAttribute(startDate + ' - ' + endDate);
        msg.push('<br />after ' + this.month);

        break;
      case 'near':
      case 'around':
        this.month = checkNearEntity(i, this.termList).toLowerCase();
        var datePickerStartDate, datePickerEndDate;

        if (lineFn.months.indexOf(this.month) > -1) {
          var mainMonthNumber = Date.getMonthNumberFromName(this.month);

          mainMonthNumber = mainMonthNumber + 1;
          var lowerMonthNumber = (mainMonthNumber > 1) ? mainMonthNumber - 1 : 1;
          var higherMonthNumber1 = (mainMonthNumber < 12) ? mainMonthNumber + 1 : 12;
          var higherMonthNumber = higherMonthNumber1 - 1;

          var higherMonthDays = Date.getDaysInMonth(lineFn.year, higherMonthNumber);

          var startDate = "01/" + lowerMonthNumber  + "/" + lineFn.year;
          var endDate =  higherMonthDays + "/" + higherMonthNumber1 + "/" + lineFn.year;
          datePickerStartDate =  lowerMonthNumber + "/01/" + lineFn.year;
          datePickerEndDate = higherMonthNumber1 + "/01/" + lineFn.year;
          lineFn.semanticZoom(startDate, endDate);
          this.commandCard.updateWhenAttribute(startDate + ' - ' + endDate);
          msg.push('<br /><br />between <input type="text" class="inputDateText" id="rangepicker1" value="' + datePickerStartDate + ' " onchange="upDateRange1(this.value);" oninput="upDateRange1(this.value);"/> and <input type="text" class="inputDateText" id="rangepicker2" value="' + datePickerEndDate + ' " onchange="upDateRange2(this.value);" oninput="upDateRange2(this.value);" />');

        }





        break;
      case 'in':
        this.month = checkNearEntity(i, this.termList);
        msg.push('in');

        break;
      case 'all':
      case 'reset':
        if (this.datasource == 'stock prices') {
          lineFn.updateDataUnit('USD');
        }
        this.valueTypes = [];
        lineFn.resetZoom();

        msg.push('all');
        break;
      case String(lineFn.year):
        lineFn.resetZoom();
        break;
      case 'Fahrenheit':
      case 'fahrenheit':
        if (this.datasource == 'temperatures') {
          if (lineFn.dataUnit != 'fahrenheit') {
            lineFn.updateDataUnit('fahrenheit');
          }
          else {
            errorMsg = 'Temperatures are already in Fahrenheit.';

          }
        }
        msg.push('Fahrenheit');
        break;
      case 'Celsius':
      case 'celsius':
        if (this.datasource == 'temperatures') {
          if (lineFn.dataUnit != 'celsius') {
            lineFn.updateDataUnit('celsius');
          }
          else {
            errorMsg = 'Temperatures are already in Celsius.';
          }
        }
        msg.push('Celsius');
        break;
      case 'euro':
      case 'euros':
      case 'eur':
        if (this.datasource == 'stock prices') {
          console.log(fx.rates);
          if (lineFn.dataUnit != 'EUR') {
            msg.push('converting from ' + lineFn.dataUnit + ' to EUR at exchange rate <input class="exchangeRateText" type="text" id="exchangerate" value="' + d3.format(".4f")(fx.convert(1, {from: lineFn.dataUnit, to: "EUR"}))  + '" />');
            lineFn.updateDataUnit('EUR');
          }
          else {
            errorMsg = 'Prices are already in Euros.';
          }
        }
        break;
      case 'pound':
      case 'pounds':
      case 'gbp':
        if (this.datasource == 'stock prices') {
          if (lineFn.dataUnit != 'GBP') {
            msg.push('converting from ' + lineFn.dataUnit + ' to GBP at exchange rate <input class="exchangeRateText" type="text" id="exchangerate" value="' + d3.format(".4f")(fx.convert(1, {from: lineFn.dataUnit, to: "GBP"})) + '" />');
            lineFn.updateDataUnit('GBP');
          }
          else {
            errorMsg = 'Prices are already in Pounds.';
          }
        }
        break;
      case 'canadian':
      case 'cad':
        if (this.datasource == 'stock prices') {
          if (lineFn.dataUnit != 'CAD') {
            msg.push('converting from ' + lineFn.dataUnit + ' to CAD at exchange rate <input class="exchangeRateText" type="text" id="exchangerate" value="' + d3.format(".4f")(fx.convert(1, {from: lineFn.dataUnit, to: "CAD"})) + '" />');
            lineFn.updateDataUnit('CAD');
          }
          else {
            errorMsg = 'Prices are already in Canadian Dollars.';
          }
        }
        break;
      case 'indian':
      case 'rupee':
      case 'rupees':
        if (this.datasource == 'stock prices') {
          if (lineFn.dataUnit != 'INR') {
            msg.push('converting from ' + lineFn.dataUnit + ' to INR at exchange rate <input class="exchangeRateText" type="text" id="exchangerate" value="' + d3.format(".4f")(fx.convert(1, {from: lineFn.dataUnit, to: "INR"})) + '" />');
            lineFn.updateDataUnit('INR');
          }
          else {
            errorMsg = 'Prices are already in Indian Rupees.';
          }
        }
        break;
      case 'yuan':
      case 'yuans':
      case 'rmb':
      case 'cny':
        if (this.datasource == 'stock prices') {
          if (lineFn.dataUnit != 'CNY') {
            msg.push('converting from ' + lineFn.dataUnit + ' to CNY at exchange rate <input class="exchangeRateText" type="text" id="exchangerate" value="' + d3.format(".4f")(fx.convert(1, {from: lineFn.dataUnit, to: "CNY"})) + '" />');
            lineFn.updateDataUnit('CNY');
          }
          else {
            errorMsg = 'Prices are already in Chinese Yuans.';
          }
        }
        break;
      case 'dollars':
      case 'dollar':
      case 'us':
      case 'usd':
        if ((this.datasource == 'stock prices') && (this.termList.indexOf("canadian") == -1)) {
          if (lineFn.dataUnit != 'USD') {
            msg.push('converting from ' + lineFn.dataUnit + ' to USD at exchange rate <input class="exchangeRateText" type="text" id="exchangerate" value="' + d3.format(".4f")(fx.convert(1, {from: lineFn.dataUnit, to: "USD"})) + '" />');
            lineFn.updateDataUnit('USD');
          }
          else {
            errorMsg = 'Prices are already in Dollars.';
          }
        }
        break;
      case 'quarter':
      case 'q':
        if ((this.termList.indexOf("first") != -1) || (this.termList.indexOf("1") != -1) || (this.termList.indexOf("1st") != -1) || (this.termList.indexOf("one") != -1)) {
          var quarterRange = lineFn.quarterRanges['Q1'];

        }
        else if ((this.termList.indexOf("second") != -1) || (this.termList.indexOf("2") != -1) || (this.termList.indexOf("2nd") != -1) || (this.termList.indexOf("second") != -1)) {
          var quarterRange = lineFn.quarterRanges['Q2'];

        }
        else if ((this.termList.indexOf("third") != -1) || (this.termList.indexOf("3") != -1) || (this.termList.indexOf("3rd") != -1) || (this.termList.indexOf("three") != -1)) {
          var quarterRange = lineFn.quarterRanges['Q3'];
        }
        else if ((this.termList.indexOf("fourth") != -1) || (this.termList.indexOf("4") != -1) || (this.termList.indexOf("4th") != -1) ||  (this.termList.indexOf("four") != -1)) {
          var quarterRange = lineFn.quarterRanges['Q4'];
        }

        var dates = quarterRange.split('*');
        var startDate = dates[0] +  "/" + lineFn.year;
        var endDate = dates[1] + "/" + lineFn.year;
        lineFn.semanticZoom(startDate, endDate);
        this.commandCard.updateWhenAttribute(startDate + ' - ' + endDate);
        msg.push(this.termList[i]);

        break;
      case 'q1':
        var quarterRange = lineFn.quarterRanges['Q1'];
        var dates = quarterRange.split('*');
        var startDate = dates[0] +  "/" + lineFn.year;
        var endDate = dates[1] + "/" + lineFn.year;
        lineFn.semanticZoom(startDate, endDate);
        this.commandCard.updateWhenAttribute(startDate + ' - ' + endDate);
        msg.push(this.termList[i]);
        break;
      case 'q2':
        var quarterRange = lineFn.quarterRanges['Q2'];
        var dates = quarterRange.split('*');
        var startDate = dates[0] +  "/" + lineFn.year;
        var endDate = dates[1] + "/" + lineFn.year;
        lineFn.semanticZoom(startDate, endDate);
        this.commandCard.updateWhenAttribute(startDate + ' - ' + endDate);
        msg.push(this.termList[i]);

        break;
      case 'q3':
        var quarterRange = lineFn.quarterRanges['Q3'];
        var dates = quarterRange.split('*');
        var startDate = dates[0] +  "/" + lineFn.year;
        var endDate = dates[1] + "/" + lineFn.year;
        lineFn.semanticZoom(startDate, endDate);
        this.commandCard.updateWhenAttribute(startDate + ' - ' + endDate);
        msg.push(this.termList[i]);
        break;
      case 'q4':
        var quarterRange = lineFn.quarterRanges['Q4'];
        var dates = quarterRange.split('*');
        var startDate = dates[0] +  "/" + lineFn.year;
        var endDate = dates[1] + "/" + lineFn.year;
        lineFn.semanticZoom(startDate, endDate);
        this.commandCard.updateWhenAttribute(startDate + ' - ' + endDate);
        msg.push(this.termList[i]);
        break;
      case 'semester':
      case 'half':
        if ((this.termList.indexOf("first") != -1) || (this.termList.indexOf("1") != -1) || (this.termList.indexOf("1st") != -1) || (this.termList.indexOf("one") != -1)) {
          var semesterRange = lineFn.semesterRanges['1'];

        }
        else if ((this.termList.indexOf("second") != -1) || (this.termList.indexOf("2") != -1) || (this.termList.indexOf("2nd") != -1) || (this.termList.indexOf("second") != -1)) {
          var semesterRange = lineFn.semesterRanges['2'];

        }
        var dates = semesterRange.split('*');
        var startDate = dates[0] +  "/" + lineFn.year;
        var endDate = dates[1] + "/" + lineFn.year;
        lineFn.semanticZoom(startDate, endDate);
        this.commandCard.updateWhenAttribute(startDate + ' - ' + endDate);
        msg.push(this.termList[i]);
        break;

      default:
        if ((lineFn.seasons.indexOf(this.termList[i]) > -1) && (this.termList.indexOf("near") == -1) && (this.termList.indexOf("around") == -1)) {
          var seasonRange, seasonZone;
          lineFn.season = this.termList[i];

          if (lineFn.hemisphere == 'Southern Hemisphere') {
            seasonRange = lineFn.southSeasonRanges[this.termList[i]];
            seasonZone = "seasons in <select onchange='main.changeSeasonFunc(this)' style='width:150px;text-align:center;margin-left:5px;margin-bottom:5px;'><option value='Northern Hemisphere' >Northern Hemisphere</option><option value='Southern Hemisphere' selected>Southern Hemisphere</option></select> . <br /> <br />";


          }
          else {
            seasonRange = lineFn.seasonRanges[this.termList[i]];
            seasonZone = "seasons in <select onchange='main.changeSeasonFunc(this)' style='width:150px;text-align:center;margin-left:5px;margin-bottom:5px;'><option value='Northern Hemisphere' selected>Northern Hemisphere</option><option value='Southern Hemisphere'>Southern Hemisphere</option></select> . <br /> <br />";

          }
          var dates = seasonRange.split('*');
          var startDate = dates[0] +  "/" + lineFn.year;
          var endDate = dates[1] + "/" + lineFn.year;

          //lineFn.semanticZoom(startDate, endDate);

          var d = dates[0].split("/");
          var datePickerStartDate = d[1] + "/" + d[0] + "/" + lineFn.year;

          d = dates[1].split("/");
          var datePickerEndDate = d[1] + "/" + d[0] + "/" + lineFn.year;
          this.commandCard.updateWhenAttribute(startDate + ' - ' + endDate);
          msg.push(seasonZone + this.termList[i] + ' is from <input type="text" class="inputDateText" id="rangepicker1" value="' + datePickerStartDate + ' " onchange="upDateSeasonRange1(this.value);" oninput="upDateSeasonRange1(this.value);"/> &nbsp;to <input type="text" class="inputDateText" id="rangepicker2" value="' + datePickerEndDate + ' " onchange="upDateSeasonRange2(this.value);" oninput="upDateSeasonRange2(this.value);" />');

        }
        if ((lineFn.months.indexOf(this.termList[i].toLowerCase()) > -1) && (this.termList.indexOf("near") == -1) && (this.termList.indexOf("before") == -1) && (this.termList.indexOf("after") == -1) && (this.termList.indexOf("around") == -1)) {
          this.month = this.termList[i];
          var mainMonthNumber = Date.getMonthNumberFromName(this.month);
          var mainMonthDays = Date.getDaysInMonth(lineFn.year, mainMonthNumber);
          mainMonthNumber = mainMonthNumber + 1;


          var startDate = "01/" + mainMonthNumber  + "/" + lineFn.year;
          var endDate =  mainMonthDays + "/" + mainMonthNumber + "/" + lineFn.year;
          lineFn.semanticZoom(startDate, endDate);

          if (mainMonthNumber.toString().length == 1) {
            mainMonthNumber = "0" + mainMonthNumber;
          }
          var datePickerStartDate = mainMonthNumber + "/01/" + lineFn.year;

          this.commandCard.updateWhenAttribute(startDate + ' - ' + endDate);
          msg.push('<input type="text" class="inputDateText" id="datepicker" value="' + datePickerStartDate + '" onchange="upDate(this.value);" oninput="upDate(this.value);"/>');

        }
        if ((this.termList.indexOf("near") == -1)  && (this.termList.indexOf("around") == -1) && (lineFn.seasons.indexOf(this.month) == -1) && (lineFn.months.indexOf(this.termList[i].toLowerCase()) == -1)) {
          msg.push(this.termList[i]);
        }


        if ((this.termList.indexOf("max") > -1) || (this.termList.indexOf("maximum") > -1) || (this.termList.indexOf("highest") > -1) || (this.termList.indexOf("high") > -1)) {
          this.valueTypes.push('max');

        }
        if ((this.termList.indexOf("min") > -1)  || (this.termList.indexOf("minimum") > -1) || (this.termList.indexOf("lowest") > -1) || (this.termList.indexOf("low") > -1)) {
          this.valueTypes.push('min');

        }
        if ((this.termList.indexOf("average") > -1) || (this.termList.indexOf("mid") > -1) || (this.termList.indexOf("middle") > -1)) {
          this.valueTypes.push('avg');

        }
        if (this.termList.indexOf("trend") > -1) {
          this.valueTypes.push('trend');

        }


        break;



    }
  }
  main.errorContainer.innerHTML = errorMsg;
  //final period at end of parsed msg
  msg.push('.');

  main.outputContainer.innerHTML = msg.join(' ');
  var picker = new Pikaday(
    {
      field: document.getElementById('datepicker'),
      //  theme: 'dark-theme',
      format: "MM/DD/YYYY",
      firstDay: 1,
      minDate: new Date(2000, 0, 1),
      maxDate: new Date(2020, 12, 31),
      yearRange: [2000,2020],
      position: 'bottom right',
      // reposition: false

    });


  $(document).on( 'keydown', function ( e ) {
    if ( e.keyCode === 27 ) { // ESC
      picker.hide();
    }});

  this.rp1 = new Pikaday(
    {
      field: document.getElementById('rangepicker1'),
      // theme: 'dark-theme',
      firstDay: 1,
      format: "MM/DD/YYYY",
      minDate: new Date(2000, 0, 1),
      maxDate: new Date(2020, 12, 31),
      yearRange: [2000,2020],
      position: 'bottom right',
      // reposition: false

    });

  this.rp2 = new Pikaday(
    {
      field: document.getElementById('rangepicker2'),
      //theme: 'dark-theme',
      firstDay: 1,
      format: "MM/DD/YYYY",
      minDate: new Date(2000, 0, 1),
      maxDate: new Date(2020, 12, 31),
      yearRange: [2000,2020],
      position: 'bottom right',
      // reposition: false

    });

  if (this.valueTypes != []) {
    lineFn.findStats(this.valueTypes);
  }
}


vizta.Main.prototype.changeTopNFunc = function(selectedObj) {

  var result = mapObj.select_top_n('magnitude', selectedObj.value);

}

vizta.Main.prototype.changeBottomNFunc = function(selectedObj) {


  var result = mapObj.select_bottom_n('magnitude', selectedObj.value);

}

vizta.Main.prototype.changeTopNRecentFunc = function(selectedObj) {

  var result = mapObj.select_top_n('earthquake_datetime', selectedObj.value);

}

vizta.Main.prototype.changeColorBy = function(selectedObj) {

  var result = barObj.colorBy(selectedObj.value);


}

vizta.Main.prototype.changeHighlightBy  = function(selectedObj, startValue, endValue) {

  var result = barObj.highlightValuesBetween(selectedObj.value, Number(startValue), Number(endValue));



}

vizta.Main.prototype.changeScatterHighlightBy  = function(selectedObj, startValue, endValue) {

  var result = scatterObj.highlightValuesBetween(selectedObj.value, Number(startValue), Number(endValue));



}


vizta.Main.prototype.changeScatterHighlightLowestBy  = function(selectedObj) {

  var result = scatterObj.min(selectedObj.value);

}


vizta.Main.prototype.processMapNlpResults = function() {
  // display termList. wordList, wordTree
  // now process the NLP results
  // To do: replace this code with a grammar
  var wordCount = this.termList.length;
  var msg = [''];
  var errorMsg = '';
  main.errorContainer.innerHTML = errorMsg;
  var result;
  var magnitudeValues = "<datalist id='numbers'><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option><option>6</option><option>7</option><option>8</option><option>9</option><option>10</option></datalist>"



  for (var i = 0; i < wordCount; ++i) {
    switch(this.termList[i]) {
      case 'large':
        this.min =   this.minLargeMag;//5;
        this.max = 10;

        msg.push('magnitude of at least <div class=sliderContainer id="magnitudeSliderContainer"> <div class=sliderBubble id="magnitudeSliderBubble"></div><div class="sliderMinValue" id="magnitudeSliderMinValue">1</div>' +
          '<div class="sliderMaxValue" id="magnitudeSliderMaxValue">10</div> <input class="slider" id="magnitudeSlider" type=range min=1 max=10 value= ' + String(this.minLargeMag) + ' oninput="main.updateMap(this.value)"></input></div> ');
        this.size = 'large';
        if ((this.termList.indexOf("near") == -1) && (this.place != '') && (this.termList.indexOf("here") == -1) ) {
          result = mapObj.select_by_Qattribute_state(this.place.toLowerCase(), 100, "magnitude", this.min, this.max);


          if (!result) {
            var pointLocation = mapObj.lookup_city_location[this.place.toLowerCase()];
            if (pointLocation != undefined) {
              this.commandCard.updateWhereAttribute(this.place);
              result = mapObj.select_near_Qattribute_point(pointLocation, 100, "magnitude", this.min, this.max);


            }

          }

        }
        else if ((this.termList.indexOf("near") == -1) && (this.termList.indexOf("here") == -1))
        {

          result = mapObj.select_by_Qattribute("magnitude", this.min, this.max);
        }
        else if ((this.termList.indexOf("here") != -1) ||  (mapObj.map_draw_selectObj.length > 0)) {
          result = mapObj.select_by_Qattribute_state("here", 100, "magnitude", this.min, this.max);

        }
        if ((!result) && (this.termList.indexOf("here") == -1)) {
          errorMsg = 'There were no marks selected that meet these criteria. Want to modify your query?';
        }
        else if ((!result) && (this.termList.indexOf("here") != -1)){
          errorMsg = 'Please draw a geographic selection on the map first.'
        }
        else {
          errorMsg = '';
        }
        mapObj.add_tooltip('selected', ['magnitude', 'Magnitude'], ['earthquake_datetime', 'Date']);

        mapObj.encode_size('selected', 'magnitude', 1.2);
        break;
      case 'largest':
      case 'biggest':
      case 'highest':
        this.size = 'largest';
        msg.push('largest ' + "<select onchange='main.changeTopNFunc(this)' style='width:70px;text-align:center;margin:10px;padding:1em;'><option value='10' selected>10</option><option value='9'>9</option><option value='8'>8</option><option value='7'>7</option><option value='6'>6</option><option value='5'>5</option><option value='4'>4</option><option value='3'>3</option><option value='2'>2</option><option value='1'>1</option></select>" );
        if ((this.termList.indexOf("near") == -1) && (this.termList.indexOf("outside") == -1) && (this.termList.indexOf("in") == -1) ) {
          result = mapObj.select_top_n('magnitude', 10);


          if (!result) {
            errorMsg = 'There were no marks selected that meet these criteria. Want to modify your query?';
          }
          else {
            errorMsg = '';
          }

          mapObj.add_tooltip('selected', ['magnitude', 'Magnitude'], ['earthquake_datetime', 'Date']);

          mapObj.encode_size('selected', 'magnitude', 1.2);
        }
        break;
      case 'smallest':
      case 'lowest':
        this.size = 'smallest';
        msg.push('smallest ' + "<select onchange='main.changeTopNFunc(this)' style='width:70px;text-align:center;margin:10px;padding:1em;'><option value='10' selected>10</option><option value='9'>9</option><option value='8'>8</option><option value='7'>7</option><option value='6'>6</option><option value='5'>5</option><option value='4'>4</option><option value='3'>3</option><option value='2'>2</option><option value='1'>1</option></select>" );

        if ((this.termList.indexOf("near") == -1) && (this.termList.indexOf("outside") == -1) && (this.termList.indexOf("in") == -1) ) {

          result = mapObj.select_bottom_n('magnitude', 10);


          if (!result) {
            errorMsg = 'There were no marks selected that meet these criteria. Want to modify your query?';
          }
          else {
            errorMsg = '';
          }
          mapObj.add_tooltip('selected', ['magnitude', 'Magnitude'], ['earthquake_datetime', 'Date']);

          mapObj.encode_size('selected', 'magnitude', 1.2);
        }
        break;

      case 'small':
        this.min = 0;
        this.max = 4;
        msg.push('magnitude of at least <div class=sliderContainer id="magnitudeSliderContainer"> <div class=sliderBubble id="magnitudeSliderBubble"></div><div class="sliderMinValue" id="magnitudeSliderMinValue">1</div>' +
          '<div class="sliderMaxValue" id="magnitudeSliderMaxValue">10</div> <input class="slider" id="magnitudeSlider" type=range min=1 max=10 value=' + String(this.min) + ' oninput="main.updateMap(this.value)"></input></div> ');


        this.size = 'small';

        if ((this.termList.indexOf("near") == -1) && (this.place != '') && (this.termList.indexOf("here") == -1))
        {

          result = mapObj.select_by_Qattribute_state(this.place.toLowerCase(), 100, "magnitude", this.min, this.max);
          this.commandCard.updateWhereAttribute(this.place);
          if (!result) {
            var pointLocation = mapObj.lookup_city_location[this.place.toLowerCase()];
            if (pointLocation != undefined) {
              result = mapObj.select_near_Qattribute_point(pointLocation, 100, "magnitude", this.min, this.max);

            }

          }
        }
        else if ((this.termList.indexOf("near") == -1) && (this.termList.indexOf("here") == -1))
        {
          result = mapObj.select_by_Qattribute("magnitude", this.min, this.max);

        }
        else if ((this.termList.indexOf("here") != -1) ||  (mapObj.map_draw_selectObj.length > 0)) {
          result = mapObj.select_by_Qattribute_state("here", 100, "magnitude", this.min, this.max);
        }
        if ((!result) && (this.termList.indexOf("here") == -1)) {
          errorMsg = 'There were no marks selected that meet these criteria. Want to modify your query?';
        }
        else if ((!result) && (this.termList.indexOf("here") != -1)) {
          errorMsg = 'Please draw a geographic selection on the map first.';
        }
        else {
          errorMsg = '';
        }
        mapObj.add_tooltip('selected', ['magnitude', 'Magnitude'], ['earthquake_datetime', 'Date']);

        mapObj.encode_size('selected', 'magnitude', 1.2);
        break;

      case 'all':
      case 'reset':
        this.min = 0;
        this.max = 10;
        this.size = '';
        this.place = '';
        mapObj.clear_selection();
        mapObj.reset_scale();
        msg.push(this.termList[i]);
        break;
      case 'less':
      case 'smaller':
      case 'more':
      case 'larger':
        var number = getNumber(this.termList);
        this.size = '';
        if ((this.termList.indexOf("less") != -1) || (this.termList.indexOf("smaller") != -1)) {
          if (number > 1) {
            this.max = number-1;
          }
          else {
            this.max = 1;
          }
          this.min = 1;
        }
        else {
          if (number < 10) {
            this.min = number + 1;
          }
          else {
            this.min = 10;
          }
          this.max = 10;

        }


        if ((this.termList.indexOf("near") == -1) && (this.place != '') && (this.termList.indexOf("here") == -1))
        {
          var pointLocation = mapObj.lookup_city_location[this.place.toLowerCase()];
          if (pointLocation != undefined) {
            this.commandCard.updateWhereAttribute(this.place);
            result = mapObj.select_near_Qattribute_point(pointLocation, 100, "magnitude", this.min, this.max);

          }
          else {
            result = mapObj.select_by_Qattribute_state(this.place.toLowerCase(), 100, "magnitude", this.min, this.max);
          }
        }
        else if ((this.termList.indexOf("here") != -1) ||  (mapObj.map_draw_selectObj.length > 0)) {
          this.commandCard.updateWhereAttribute("here");
          result = mapObj.select_by_Qattribute_state("here", 100, "magnitude", this.min, this.max);
        }
        else if (this.termList.indexOf("in") == -1)
        {

          result = mapObj.select_by_Qattribute("magnitude", this.min, this.max);
        }
        if ((!result) && (this.termList.indexOf("here") == -1)) {
          errorMsg = 'There were no marks selected that meet these criteria. Want to modify your query?';
        }
        else if ((!result) && (this.termList.indexOf("here") != -1)) {
          errorMsg = 'Please draw a geographic selection on the map first.';
        }
        else {
          errorMsg = '';
        }
        mapObj.add_tooltip('selected', ['magnitude', 'Magnitude'], ['earthquake_datetime', 'Date']);

        mapObj.encode_size('selected', 'magnitude', 1.2);

        break;
      case 'between':
        var numbers = getRange(this.termList);
        this.size = '';
        this.min = numbers[0];
        this.max = numbers[1];


        if (this.termList.indexOf("with") != -1) {
          var a = getAttribute('with', 'between', this.inputText.value);
        }
        else {
          var a = getAttribute('', 'between', this.inputText.value);
        }
        this.dataattribute = a[1].trim();
        msg.push('between');
        if ((this.termList.indexOf("near") == -1) && (this.place != '') && (this.termList.indexOf("here") == -1))
        {
          var pointLocation = mapObj.lookup_city_location[this.place.toLowerCase()];
          if (pointLocation != undefined) {
            result = mapObj.select_near_Qattribute_point(pointLocation, 100, "magnitude", this.min, this.max);

          }
          else {
            result = mapObj.select_by_Qattribute_state(this.place.toLowerCase(), 100, "magnitude", this.min, this.max);
          }
        }
        else if ((this.termList.indexOf("here") != -1) ||  (mapObj.map_draw_selectObj.length > 0)) {
          result = mapObj.select_by_Qattribute_state("here", 100, "magnitude", this.min, this.max);
        }
        else if (this.termList.indexOf("in") == -1)
        {

          result = mapObj.select_by_Qattribute("magnitude", this.min, this.max);
        }
        if ((!result) && (this.termList.indexOf("here") == -1)) {
          errorMsg = 'There were no marks selected that meet these criteria. Want to modify your query?';
        }
        else if ((!result) && (this.termList.indexOf("here") != -1)) {
          errorMsg = 'Please draw a geographic selection on the map first.';
        }
        else {
          errorMsg = '';
        }
        mapObj.add_tooltip('selected', ['magnitude', 'Magnitude'], ['earthquake_datetime', 'Date']);

        mapObj.encode_size('selected', 'magnitude', 1.2);
        break;
      case 'near':
      case 'in':
      case 'outside':
        var bufferDist = 0;
        if ((this.termList.indexOf("near") != -1) || (this.termList.indexOf("outside") != -1))
          bufferDist = 100;
        if (this.termList.indexOf("in") != -1) {
          bufferDist = 1;

        }

        if ((this.termList.indexOf("small") == -1) && (this.termList.indexOf("large") == -1) && (this.termList.indexOf("largest") == -1) && (this.termList.indexOf("highest") == -1) && (this.termList.indexOf("biggest") == -1) && (this.termList.indexOf("smallest") == -1) && (this.termList.indexOf("lowest") == -1)) {
          switch(this.size) {
            case 'large':

              msg.push('magnitude of at least <div class=sliderContainer id="magnitudeSliderContainer"> <div class=sliderBubble id="magnitudeSliderBubble"></div><div class="sliderMinValue" id="magnitudeSliderMinValue">1</div>' +
                '<div class="sliderMaxValue" id="magnitudeSliderMaxValue">10</div> <input class="slider" id="magnitudeSlider" type=range min=1 max=10 value= ' +  String(this.minLargeMag) + ' oninput="main.updateMap(this.value)"></input></div> ');
              break;
            case 'small':
              msg.push('magnitude of at least <div class=sliderContainer id="magnitudeSliderContainer"> <div class=sliderBubble id="magnitudeSliderBubble"></div><div class="sliderMinValue" id="magnitudeSliderMinValue">1</div>' +
                '<div class="sliderMaxValue" id="magnitudeSliderMaxValue">10</div> <input class="slider" id="magnitudeSlider" type=range min=1 max=10 value=0 oninput="main.updateMap(this.value)"></input></div> ');
              break;
            default:
              msg.push(this.termList[i]);
              break;

          }


        }
        if (this.termList[i] == 'near') {
          msg.push('within <div class=sliderContainer id="nearSliderContainer"> <div class=sliderBubble id="nearSliderBubble"></div><div class="sliderMinValue" id="nearSliderMinValue">0</div>' +
            '<div class="sliderMaxValue" id="nearSliderMaxValue">200</div> <input class="slider" id="nearSlider" type="range" min=0 max=200 value=100 oninput="main.updateMapDistance(this.value)"></input></div> miles of ');
        }
        else if (this.termList[i] == 'outside') {
          msg.push('<div class=sliderContainer id="nearSliderContainer"> <div class=sliderBubble id="nearSliderBubble"></div><div class="sliderMinValue" id="nearSliderMinValue">0</div>' +
            '<div class="sliderMaxValue" id="nearSliderMaxValue">200</div> <input class="slider" id="nearSlider" type="range" min=0 max=200 value=100 oninput="main.updateMapDistance(this.value)"></input></div> miles of ');
        }

        this.place = checkNearEntity(i, this.termList);


        if (this.place == 'me' || this.place == 'my location') {
          mapObj.getUserLocation();
          // todo: return true or false from get user location. support large earthquakes by populating place.
          result = true;
          mapObj.add_tooltip('selected', ['magnitude', 'Magnitude'], ['earthquake_datetime', 'Date']);

          mapObj.encode_size('selected', 'magnitude', 1.2);


          this.commandCard.updateWhereAttribute(this.place);


        }
        else {

          if (this.size == 'large')
          {


            if ((this.termList.indexOf("near") != -1) || (this.termList.indexOf("in") != -1) ) {
              var newPlace = checkNearEntity(i, this.termList);

              if (newPlace != '') {
                this.place = newPlace;
              }
              if (this.place != '') {
                this.commandCard.updateWhereAttribute(this.place);

                result = mapObj.select_by_Qattribute_state(this.place.toLowerCase(), bufferDist, "magnitude",  this.minLargeMag, 10);
                if (this.termList.indexOf("in") != -1) {
                  msg.push('in');
                }
              }

            }
            else if (this.termList.indexOf("outside") != -1)
            {
              if (this.place != '') {
                this.commandCard.updateWhereAttribute(this.place);
                result = mapObj.select_outside_Qattribute_state(this.place.toLowerCase(), bufferDist, 'magnitude',  this.minLargeMag, 10);
              }
            }
            if (!result) {

              var pointLocation = mapObj.lookup_city_location[this.place.toLowerCase()];
              if (pointLocation != undefined) {
                this.commandCard.updateWhereAttribute(this.place);
                result = mapObj.select_near_Qattribute_point(pointLocation, bufferDist, "magnitude",  this.minLargeMag, 10);
              }
            }

          }
          else if (this.size == 'small')
          {

            if ((this.termList.indexOf("near") != -1) || (this.termList.indexOf("in") != -1)) {
              if (this.place != '') {
                this.commandCard.updateWhereAttribute(this.place);
                result = mapObj.select_by_Qattribute_state(this.place.toLowerCase(), bufferDist, "magnitude", 0, 4);
              }
            }
            else if (this.termList.indexOf("outside") != -1)
            {
              if (this.place != '') {
                this.commandCard.updateWhereAttribute(this.place);
                result = mapObj.select_outside_Qattribute_state(this.place.toLowerCase(), bufferDist, "magnitude", 0, 4);
              }
            }
            if (!result) {
              var pointLocation = mapObj.lookup_city_location[this.place.toLowerCase()];
              if (pointLocation != undefined) {
                this.commandCard.updateWhereAttribute(this.place);
                result = mapObj.select_near_Qattribute_point(pointLocation, bufferDist, "magnitude", 0, 4);
              }

            }
          }
          ////
          else if (this.size == 'largest')
          {
            // handling 'near' and 'in' cases
            if ((this.termList.indexOf("near") != -1) || (this.termList.indexOf("in") != -1)) {
              if (this.termList.indexOf("in") != -1) {
                msg.push('in');
                if (this.place != '') {
                  this.commandCard.updateWhereAttribute(this.place);
                  result = mapObj.select_top_n_by_state('magnitude', 10, this.place.toLowerCase(), bufferDist)
                }
              }
              if (this.termList.indexOf("near") != -1) {
                if (this.place != '') {
                  this.commandCard.updateWhereAttribute(this.place);
                  result = mapObj.select_top_n_by_state('magnitude', 10, this.place.toLowerCase(), bufferDist)

                  if (!result) {
                    var pointLocation = mapObj.lookup_city_location[this.place.toLowerCase()];
                    if (pointLocation != undefined) {
                      this.commandCard.updateWhereAttribute(this.place);
                      result = mapObj.select_top_n_near_point('magnitude', 10, pointLocation, bufferDist);
                    }

                  }
                }


              }

            }
            // handling 'outside' case
            else if (this.termList.indexOf("outside") != -1)
            {
              msg.push('outside');
              if (this.place != '') {
                this.commandCard.updateWhereAttribute(this.place);
                result = mapObj.select_top_n_outside_state('magnitude', 10, this.place.toLowerCase(), bufferDist);

                if (!result) {
                  var pointLocation = mapObj.lookup_city_location[this.place.toLowerCase()];
                  if (pointLocation != undefined) {
                    this.commandCard.updateWhereAttribute(this.place);
                    result = mapObj.select_top_n_outside_point('magnitude', 10, pointLocation, bufferDist);
                  }

                }
              }
            }

          }
          else if (this.size == 'smallest')
          {
            // handling 'near' and 'in' cases
            if ((this.termList.indexOf("near") != -1) || (this.termList.indexOf("in") != -1)) {
              if (this.termList.indexOf("in") != -1) {
                msg.push('in');
                if (this.place != '') {
                  this.commandCard.updateWhereAttribute(this.place);
                  result = mapObj.select_bottom_n_by_state('magnitude', 10, this.place.toLowerCase(), bufferDist)
                }
              }
              if (this.termList.indexOf("near") != -1) {
                if (this.place != '') {
                  this.commandCard.updateWhereAttribute(this.place);
                  result = mapObj.select_bottom_n_by_state('magnitude', 10, this.place.toLowerCase(), bufferDist)

                  if (!result) {
                    var pointLocation = mapObj.lookup_city_location[this.place.toLowerCase()];
                    if (pointLocation != undefined) {
                      this.commandCard.updateWhereAttribute(this.place);
                      result = mapObj.select_bottom_n_near_point('magnitude', 10, pointLocation, bufferDist);
                    }

                  }
                }


              }

            }
            // handling 'outside' case
            else if (this.termList.indexOf("outside") != -1)
            {
              msg.push('outside');
              if (this.place != '') {
                this.commandCard.updateWhereAttribute(this.place);
                result = mapObj.select_bottom_n_outside_state('magnitude', 10, this.place.toLowerCase(), bufferDist);

                if (!result) {
                  var pointLocation = mapObj.lookup_city_location[this.place.toLowerCase()];
                  if (pointLocation != undefined) {
                    this.commandCard.updateWhereAttribute(this.place);
                    result = mapObj.select_bottom_n_outside_point('magnitude', 10, pointLocation, bufferDist);
                  }

                }
              }
            }

          }
          else
          {

            if ((this.termList.indexOf("near") != -1) || (this.termList.indexOf("in") != -1) ) {
              if (this.place != '') {
                this.commandCard.updateWhereAttribute(this.place);
                //pass attribute name "last_sale_price", magnitude

                result = mapObj.select_by_Qattribute_state(this.place.toLowerCase(), bufferDist, "magnitude", this.min, this.max);

              }
            }
            else if (this.termList.indexOf("outside") != -1)
            {
              if (this.place != '') {
                this.commandCard.updateWhereAttribute(this.place);
                result = mapObj.select_outside_Qattribute_state(this.place.toLowerCase(), bufferDist, "magnitude", this.min, this.max);

              }
            }
            if (!result) {
              var pointLocation = mapObj.lookup_city_location[this.place.toLowerCase() ];
              if (pointLocation != undefined) {
                this.commandCard.updateWhereAttribute(this.place);
                result = mapObj.select_near_point(pointLocation, bufferDist);
              }

            }
          }
        }
        if (!result) {
          if (this.place != 'here') {
            errorMsg = 'There were no marks selected that meet these criteria. Want to modify your query?';
          }
          else {
            errorMsg = 'Please draw a geographic selection on the map first.';
          }
        }
        else {
          errorMsg = '';
        }
        mapObj.add_tooltip('selected', ['magnitude', 'Magnitude'], ['earthquake_datetime', 'Date']);

        mapObj.encode_size('selected', 'magnitude', 1.2);
        break;
      case 'recent':
        if (this.termList.indexOf("recent") != -1)
        {
          msg.push(' ' + "<select onchange='main.changeTopNRecentFunc(this)'><option value='10' selected>10</option><option value='9'>9</option><option value='8'>8</option><option value='7'>7</option><option value='6'>6</option><option value='5'>5</option><option value='4'>4</option><option value='3'>3</option><option value='2'>2</option><option value='1'>1</option></select>" + ' most recent');
          result = mapObj.select_top_n('earthquake_datetime', 10);
          if (!result) {
            errorMsg = 'There were no marks selected that meet these criteria. Want to modify your query?';
          }
          else {
            errorMsg = '';
          }
        }
        break;
      case 'aggregate':
        mapObj.draw_hex();
        msg.push(this.termList[i]);
        break;
      case 'bored':
      case 'entertaining':
      case 'fun':
        errorMsg = 'Arrr! Why are you bored (matey)?';
        mapObj.set_basemap('mapbox.pirates');
        msg.push(this.termList[i]);
        break;
      default:
        msg.push(this.termList[i]);
        break;
    }
  }
  //final period at end of parsed msg
  msg.push('.');


  main.outputContainer.innerHTML = msg.join(' ');

  // get the bubble and the slider and add the event handler

  // get the slider by id "nearSlider"
  var nearSliderBubble = document.querySelector('#nearSliderBubble');
  var nearSlider = document.querySelector('#nearSlider');

  if (nearSlider) {
    nearSlider.addEventListener('input', main.handleNearSliderUpdate);
    main.handleNearSliderUpdate(); // call it initially to set the bubble's position
  }

  // get the slider by id "magnitudeSlider"
  var magnitudeSliderBubble = document.querySelector('#magnitudeSliderBubble');
  var magnitudeSlider = document.querySelector('#magnitudeSlider');

  if (magnitudeSlider) {
    magnitudeSlider.addEventListener('input', main.handleMagnitudeSliderUpdate);
    main.handleMagnitudeSliderUpdate(); // call it initially to set the bubble's position
  }

  main.errorContainer.innerHTML = errorMsg;


};

vizta.Main.prototype.handleNearSliderUpdate = function(e) {
  // get the slider by id "nearSlider"
  var nearSliderBubble = document.querySelector('#nearSliderBubble');
  var nearSlider = document.querySelector('#nearSlider');

  nearSliderBubble.innerHTML = nearSlider.value;

  nearSliderBubble.style.left =
    (nearSlider.value / (nearSlider.max - nearSlider.min)) * (nearSlider.offsetWidth) + (nearSlider.getBoundingClientRect().left  - nearSlider.width - 26)  - 30 + 'px';
}


vizta.Main.prototype.handleMagnitudeSliderUpdate = function(e) {
  // get the slider by id "nearSlider"
  var magnitudeSliderBubble = document.querySelector('#magnitudeSliderBubble');
  var magnitudeSlider = document.querySelector('#magnitudeSlider');
  magnitudeSliderBubble.innerHTML = magnitudeSlider.value;

  magnitudeSliderBubble.style.left =
    (magnitudeSlider.value / (magnitudeSlider.max - magnitudeSlider.min)) * (magnitudeSlider.offsetWidth) + (magnitudeSlider.getBoundingClientRect().left  - magnitudeSlider.width - 18)  - 58 + 'px';
}

//scatter
vizta.Main.prototype.processScatterNlpResults = function() {
  // display termList. wordList, wordTree
  var wordCount = this.termList.length;
  var msg = [''];
  var errorMsg = '';
  var countrylist = [];
  var yearlist = [];
  main.errorContainer.innerHTML = errorMsg;
  var startYear='', endYear='';
  scatterObj.hideRegressionLine();
  var keywords = ['find', 'highlight','show', 'from', 'greater','less','lesser','larger','bigger','smaller','before','after','highest','biggest', 'maximum', 'max', 'largest','lowest', 'smallest', 'minimum', 'min', 'regression', 'trend','trendline','correlation','correlations','correlate','fit', 'curve', 'reset', 'all'];
  for (var i = 0; i < wordCount; ++i) {
    switch(this.termList[i]) {
      case 'find':
      case 'highlight':
      case 'show':
      case 'between':
      case 'from':
      case 'greater':
      case 'less':
      case 'lesser':
      case 'larger':
      case 'bigger':
      case 'smaller':
      case 'in':
      case 'before':
      case 'after':
      case 'highest':
      case 'biggest':
      case 'largest':
      case 'lowest':
      case 'smallest':
      case 'maximum':
      case 'max':
      case 'minimum':
      case 'min':
        if (this.termList.indexOf("in") != -1) {
          var yearVar = checkYearEntity(this.termList, scatterObj.years);
          if (yearVar.length == 1) {
            if (yearVar[0] >= 1800 && yearVar[0] <= 2010) {
              this.commandCard.updateWhenAttribute(yearVar[0]);
              scatterObj.goToYear(yearVar[0], false);
            }
            else
              errorMsg = 'Pick a year between 1800 and 2010.';
            if ((this.termList.indexOf("smallest") == -1) && (this.termList.indexOf("lowest") == -1) && (this.termList.indexOf("biggest") == -1) && (this.termList.indexOf("largest") == -1) && (this.termList.indexOf("highest") == -1) && (this.termList.indexOf("lowest") == -1) && (this.termList.indexOf("greater") == -1) && (this.termList.indexOf("less") == -1) && (this.termList.indexOf("larger") == -1) && (this.termList.indexOf("bigger") == -1) && (this.termList.indexOf("smaller") == -1) && (this.termList.indexOf("lesser") == -1)) {


              //msg.push(this.termList[i]);
              //	msg.push(yearVar[0]);
            }
          }
          if (yearVar.length == 2) {
            if (yearVar[0] >= 1800 && yearVar[1] <= 2010) {
              this.commandCard.updateWhenAttribute(yearVar[0] + ' - ' + yearVar[1]);
              scatterObj.betweenYears(yearVar[0], yearVar[1], false);
            }
            else
              errorMsg = 'Pick a year between 1800 and 2010.';
            if ((this.termList.indexOf("smallest") == -1) && (this.termList.indexOf("lowest") == -1) && (this.termList.indexOf("biggest") == -1) && (this.termList.indexOf("largest") == -1) && (this.termList.indexOf("highest") == -1) && (this.termList.indexOf("lowest") == -1) && (this.termList.indexOf("greater") == -1) && (this.termList.indexOf("less") == -1) && (this.termList.indexOf("larger") == -1) && (this.termList.indexOf("bigger") == -1) && (this.termList.indexOf("smaller") == -1) && (this.termList.indexOf("lesser") == -1)) {
              msg.push('between ' + yearVar[0] + ' and ' + yearVar[1]);
            }
          }

        }

        if ((this.termList.indexOf("greater") == -1) && (this.termList.indexOf("less") == -1) && (this.termList.indexOf("larger") == -1) && (this.termList.indexOf("bigger") == -1) && (this.termList.indexOf("smaller") == -1) && (this.termList.indexOf("lesser") == -1)) {
          // first get the years: could be either in, before or after
          if ((this.termList[i] == "before") || (this.termList[i] == "after")) {

            var numberPattern = /\d+/g;
            var number = this.inputText.value.match( numberPattern );
            var minYear, maxYear;

            if (this.termList[i] == "before") {
              if (parseInt(number[0]) > 1800) {
                maxYear = parseInt(number[0]) - 1;
              }
              else {
                maxYear = 1800;
              }
              minYear = 1800;

            }
            else { //it's after

              if (parseInt(number[0]) < 2010) {
                minYear = parseInt(number[0]) + 1;
              }
              else {
                minYear = 2010;
              }

              maxYear = 2010;

            }
            scatterObj.betweenYears(minYear.toString(), maxYear.toString(), false);
            this.commandCard.updateWhenAttribute(minYear.toString() + ' - ' + maxYear.toString());
            msg.push(this.termList[i]);
            msg.push(number[0]);
          }
          else if ((this.termList[i] == "between") || (this.termList[i] == "from")) {
            var numberPattern = /\d+/g;
            var numbers = this.inputText.value.match( numberPattern );
            var startValue, endValue, matches = [];

            if ((numbers != undefined) && (numbers.length == 2)) {
              startValue = numbers[0];
              endValue = numbers[1];

              for (var j = 0; j < this.termList.length; j++) {
                if (this.termList[j] != 'in' && this.termList[j] != 'to' && this.termList[j] != 'and' && this.termList[j] != 'between' && this.termList[j] != 'find' && this.termList[j] != 'show' && this.termList[j] != 'highlight') {
                  var results = fuzzy.filter(this.termList[j], scatterObj.dataAttributes);
                  if (results.length > 0)
                    matches = results.map(function(el) { return el.string; });
                }
              }

              if (matches.length > 0) {
                var inRange1 = scatterObj.checkRange(matches[0], Number(startValue), Number(endValue));
                msg.push(' ' + "<select onchange='main.changeScatterHighlightBy(this," + startValue + "," + endValue + ")'><option value='" + matches[0] + "' selected>" + matches[0] + "</option>");
                for (var j = 1; j < matches.length; j++) {

                  msg.push("<option value='" + matches[j] + "'>" + matches[j] + "</option>");
                }
                msg.push("</select> between " + startValue + " and " + endValue);
                if (inRange1) {
                  var result = scatterObj.highlightValuesBetween(matches[0], Number(startValue), Number(endValue));
                  this.commandCard.updateWhatAttribute(matches[0]);
                }
                else if ((!inRange1) || (!result)){
                  var correctRange = scatterObj.getRangeForAttr(matches[0]);
                  errorMsg = 'No results found for that range. Try a range between ' + correctRange[0] + ' and ' + correctRange[1] + '.'
                }

              }
              else {
                if (startValue >= 1800 && endValue <= 2010) {
                  scatterObj.betweenYears(startValue, endValue, false);
                }
                else {
                  errorMsg = 'No results found.';
                }

              }


            }
            //msg.push(this.termList[i]);

          }
          // lowest...
          if ((this.termList[i] == "lowest")  || (this.termList[i] == "smallest") || (this.termList[i] == "minimum") || (this.termList[i] == "min")) {
            msg.push(this.termList[i]);
            var matches;
            for (var j = 0; j < this.termList.length; j++) {
              if (this.termList[j] != 'in' && this.termList[j] != 'to' && this.termList[j] != 'and' && this.termList[j] != 'between' && this.termList[j] != 'find' && this.termList[j] != 'show' && this.termList[j] != 'highlight') {

                var results = fuzzy.filter(this.termList[j], scatterObj.dataAttributes);
                if (results.length > 0)
                  matches = results.map(function(el) { return el.string; });
              }
            }

            if (matches.length > 0) {

              msg.push(' ' + "<select onchange='main.changeScatterHighlightLowestBy(this)'><option value='" + matches[0] + "' selected>" + matches[0] + "</option>");
              for (var j = 1; j < matches.length; j++) {

                msg.push("<option value='" + matches[j] + "'>" + matches[j] + "</option>");
              }
              msg.push("</select>");
              this.commandCard.updateWhatAttribute(matches[0]);
              scatterObj.min(matches[0]);


            }
            else {
              errorMsg = 'No attribute found.';
            }
          }


          if ((this.termList[i] == "highest")  || (this.termList[i] == "biggest") || (this.termList[i] == "largest") || (this.termList[i] == "maximum") || (this.termList[i] == "max")) {
            msg.push(this.termList[i]);
            var matches;
            for (var j = 0; j < this.termList.length; j++) {
              if (this.termList[j] != 'in' && this.termList[j] != 'to' && this.termList[j] != 'and' && this.termList[j] != 'between' && this.termList[j] != 'find' && this.termList[j] != 'show' && this.termList[j] != 'highlight') {

                var results = fuzzy.filter(this.termList[j], scatterObj.dataAttributes);
                if (results.length > 0)
                  matches = results.map(function(el) { return el.string; });
              }
            }

            if (matches.length > 0) {

              msg.push(' ' + "<select onchange='main.changeScatterHighlightLowestBy(this)'><option value='" + matches[0] + "' selected>" + matches[0] + "</option>");
              for (var j = 1; j < matches.length; j++) {

                msg.push("<option value='" + matches[j] + "'>" + matches[j] + "</option>");
              }
              msg.push("</select>");
              this.commandCard.updateWhatAttribute(matches[0]);
              scatterObj.max(matches[0]);


            }
            else {
              errorMsg = 'No attribute found.';
            }
          }
        }
        // greater
        if ((this.termList[i] == "greater") || (this.termList[i] == "larger")  || (this.termList[i] == "bigger")) {
          if (this.termList.indexOf("than") != -1) {
            // get number right after 'than'
            var m = this.inputText.value.match(/than(\s+\d+)/);
            var startValue = m[1], endValue, correctRange, matches;

            for (var j = 0; j < this.termList.length; j++) {
              if (this.termList[j] != 'in' && this.termList[j] != 'to' && this.termList[j] != 'and' && this.termList[j] != 'between' && this.termList[j] != 'find' && this.termList[j] != 'show' && this.termList[j] != 'highlight') {
                var results = fuzzy.filter(this.termList[j], scatterObj.dataAttributes);
                //var options = { pre: '<', post: '>' };
                if (results.length > 0)
                  matches = results.map(function(el) { return el.string; });
              }
            }
            if (matches.length > 0) {
              correctRange = scatterObj.getRangeForAttr(matches[0]);
              endValue = correctRange[1];
              var inRange1 = scatterObj.checkRange(matches[0], Number(startValue), Number(endValue));

              msg.push(' ' + "<select onchange='main.changeScatterHighlightBy(this," + startValue + "," + endValue + ")'><option value='" + matches[0] + "' selected>" + matches[0] + "</option>");
              for (var j = 1; j < matches.length; j++) {

                msg.push("<option value='" + matches[j] + "'>" + matches[j] + "</option>");
              }
              msg.push("</select> between " + startValue + " and " + endValue);
              if (inRange1) {
                this.commandCard.updateWhatAttribute(matches[0]);
                scatterObj.highlightValuesBetween(matches[0], Number(startValue), Number(endValue));
              }
              else if (!inRange1){
                var correctRange = scatterObj.getRangeForAttr(matches[0]);
                errorMsg = 'No results found for that range. Try a range between ' + correctRange[0] + ' and ' + correctRange[1] + '.'
              }

            }

            else {
              errorMsg = 'No results found.';

            }
          }

          ///

        }// end greater
        // lesser
        else if ((this.termList[i] == "lesser") || (this.termList[i] == "less") || (this.termList[i] == "smaller")) {

          if (this.termList.indexOf("than") != -1) {

            // get number right after 'than'
            var m = this.inputText.value.match(/than(\s+\d+)/);
            var endValue = m[1], startValue, correctRange, matches;
            for (var j = 0; j < this.termList.length; j++) {
              if (this.termList[j] != 'less' && this.termList[j] != 'in' && this.termList[j] != 'to' && this.termList[j] != 'and' && this.termList[j] != 'between' && this.termList[j] != 'find' && this.termList[j] != 'show' && this.termList[j] != 'highlight') {

                var results = fuzzy.filter(this.termList[j], scatterObj.dataAttributes);
                if (results.length > 0)
                  matches = results.map(function(el) { return el.string; });
              }
            }

            if (matches.length > 0) {
              correctRange = scatterObj.getRangeForAttr(matches[0]);
              startValue = correctRange[0];
              var inRange1 = scatterObj.checkMaxRange(matches[0], Number(endValue));
              msg.push(' ' + "<select><option value='" + matches[0] + "' selected>" + matches[0] + "</option>");
              for (var j = 1; j < matches.length; j++) {

                msg.push("<option value='" + matches[j] + "'>" + matches[j] + "</option>");
              }
              msg.push("</select> between " + startValue + " and " + endValue);
              if (inRange1) {
                this.commandCard.updateWhatAttribute(matches[0]);
                scatterObj.highlightValuesBetween(matches[0], Number(startValue), Number(endValue));
              }
              else if (!inRange1){

                errorMsg = 'No results found for that range. Try a range between ' + correctRange[0] + ' and ' + correctRange[1] + '.'
              }

            }

            else {
              errorMsg = 'No such data attribute or value exists.'
            }
          }

        }

        break;



      case 'regression':
      case 'trend':
      case 'trendline':
      case 'correlation':
      case 'correlations':
      case 'correlate':
      case 'fit':
      case 'curve':
        scatterObj.showRegressionLine();
        msg.push(this.termList[i]);
        break;
      case 'reset':
      case 'all':
        var errorMsg = '';
        this.errorContainer.innerHTML = errorMsg;
        this.vizContainer.innerHTML = "";
        scatterObj.initializeScatterPlot();
        msg.push(this.termList[i]);
        break;

      default:
        for (var j = 0; j < scatterObj.countries.length; j++) {
          var str = this.inputText.value.toLowerCase();
          if (str.indexOf(scatterObj.countries[j]) > -1) {
            if (countrylist.indexOf(scatterObj.countries[j]) == -1) {
              countrylist.push(scatterObj.countries[j]);
            }
          }
        }
        if (scatterObj.years.indexOf(this.termList[i] ) != -1) {
          yearlist.push(this.termList[i]);
        }


        break;



    }

  }



  if (countrylist.length > 0) {
    showHideLabels(countrylist);
    for (var j = 0; j < countrylist.length; j++ ) {
      countrylist[j] = toTitleCase(countrylist[j]);
    }
    this.commandCard.updateWhereAttribute(countrylist.join(', '));
  }

  if ((yearlist.length == 1) && (this.termList.indexOf("in") == -1) && (this.termList.indexOf("before") == -1) && (this.termList.indexOf("after") == -1))  {
    // single year

    scatterObj.goToYear(yearlist[0], false);
    this.commandCard.updateWhenAttribute(yearlist[0]);
    msg.push(yearlist[0]);

  }
  if ((yearlist.length == 2) && (this.termList.indexOf("between") == -1) && (this.termList.indexOf("from") == -1) )  {
    scatterObj.betweenYears(yearlist[0], yearlist[1], false);
    this.commandCard.updateWhenAttribute(yearlist[0] + ' - ' + yearlist[1]);
    msg.push('between ' + yearlist[0] + ' and ' + yearlist[1]);
  }
  main.errorContainer.innerHTML = errorMsg;
  //final period at end of parsed msg
  msg.push('.');

  main.outputContainer.innerHTML = msg.join(' ');

}


//bar
vizta.Main.prototype.processBarNlpResults = function() {
  // display termList. wordList, wordTree
  var wordCount = this.termList.length;
  var msg = [''];
  var errorMsg = '';
  var countrylist = [];
  main.errorContainer.innerHTML = errorMsg;

  for (var i = 0; i < wordCount; ++i) {
    switch(this.termList[i]) {
      case 'sort':
      case 'ascending':
      case 'descending':
        // check if user asks ascending or descending
        if (this.termList.indexOf("from") != -1) {
          var fromIndex = this.termList.indexOf("from");
          var sortOrder = checkNearEntity(fromIndex, this.termList);
          switch(sortOrder) {
            case 'largest':
            case 'biggest':
            case 'highest':
            case 'large':
            case 'big':
            case 'high':
            case 'most':
            case 'descending':
              barObj.sortBars(barObj.yAtt, "desc");
              break;
            case 'smallest':
            case 'lowest':
            case 'small':
            case 'low':
            case 'least':
            case 'ascending':
              barObj.sortBars(barObj.yAtt, "asc");
              break;
            default:
              barObj.sortBars(barObj.yAtt, "desc");
              break;

          }


        }
        else {
          // else default to desc
          if (this.termList.indexOf("ascending") != -1) {
            barObj.sortBars(barObj.yAtt, "asc");
          }
          else {
            barObj.sortBars(barObj.yAtt, "desc");
          }
        }
        // TO DO: and have a widget to change to asc if need be.
        msg.push(this.termList[i]);
        break;
      case 'color':
      case 'colour':
        if (this.termList.indexOf("by") != -1) {
          var byIndex = this.termList.indexOf("by");
          var attr = checkNearEntity(byIndex, this.termList);

          var f = new Fuse(barObj.dataAttributes);
          var results = f.search(attr);
          //console.log(results);
          // get the top ranked result. To do: add the ranked list to an ambiguity widget.
          result = results[0];
          if (result != undefined) {
            msg.push(' ' + "color by <select onchange='main.changeColorBy(this)'><option value='" + barObj.dataAttributes[result] + "' selected>" + barObj.dataAttributes[result] + "</option>");
            for (var j = 1; j < results.length; j++) {
              var idx = results[j];
              msg.push("<option value='" + barObj.dataAttributes[idx] + "'>" + barObj.dataAttributes[idx] + "</option>");
            }
            msg.push("</select>");
            barObj.colorBy(barObj.dataAttributes[result]);
          }
          else {
            errorMsg = 'Cannot find the attribute in the dataset.'
          }
        }
        break;
      case 'find':
      case 'highlight':
      case 'show':
      case 'between':
      case 'from':
      case 'greater':
      case 'less':
      case 'lesser':
      case 'larger':
      case 'bigger':
      case 'smaller':
        // check if there is a 'between' or a 'from' for barObj.highlightValuesBetween("GDPPerCapita", 5000, 10000);
        if ((this.termList.indexOf("greater") == -1) && (this.termList.indexOf("less") == -1) && (this.termList.indexOf("larger") == -1) && (this.termList.indexOf("bigger") == -1) && (this.termList.indexOf("smaller") == -1) && (this.termList.indexOf("lesser") == -1)) {
          var numberPattern = /\d+/g;
          var numbers = this.inputText.value.match( numberPattern );
          var startValue, endValue, matches;

          // 'between' query
          if ((numbers != undefined) && (numbers.length == 2)) {
            startValue = numbers[0];
            endValue = numbers[1];

            for (var j = 0; j < this.termList.length; j++) {
              if (this.termList[j] != 'to' && this.termList[j] != 'and' && this.termList[j] != 'between' && this.termList[j] != 'find' && this.termList[j] != 'show' && this.termList[j] != 'highlight') {
                var results = fuzzy.filter(this.termList[j], barObj.dataAttributes);
                //	var options = { pre: '<', post: '>' };
                if (results.length > 0)
                  matches = results.map(function(el) { return el.string; });
              }
            }
            if (matches.length > 0) {
              var inRange1 = barObj.checkRange(matches[0], Number(startValue), Number(endValue));
              if ((this.termList[i] != 'find') && (this.termList[i] != 'highlight') && (this.termList[i] != 'and')) {
                msg.push(' ' + "<select onchange='main.changeHighlightBy(this," + startValue + "," + endValue + ")'><option value='" + matches[0] + "' selected>" + matches[0] + "</option>");
                for (var j = 1; j < matches.length; j++) {

                  msg.push("<option value='" + matches[j] + "'>" + matches[j] + "</option>");
                }
                msg.push("</select> between " + startValue + " and " + endValue);
              }
              if (inRange1) {
                var result = barObj.highlightValuesBetween(matches[0], Number(startValue), Number(endValue));
              }
              else if ((!inRange1) || (!result)){
                var correctRange = barObj.getRangeForAttr(matches[0]);
                errorMsg = 'No results found for that range. Try a range between ' + correctRange[0] + ' and ' + correctRange[1] + '.'
              }

            }


          }
          else {
            var str = this.inputText.value.toLowerCase();
            var validValues = barObj.validCountries(str);
            if (validValues.length > 0) {
              barObj.highlightEqualTo("Country", validValues);
            }
            else {
              validValues = barObj.validSubRegions(str);
              if (validValues.length > 0) {
                barObj.highlightEqualTo("SubRegion", validValues);
              }
              else {
                if ((this.termList.indexOf('largest') == -1) && (this.termList.indexOf('smallest') == -1)) {
                  errorMsg = 'No such data attribute or value exists.'
                }
              }
            }

          }//else block


        }
        else if ((this.termList[i] == "greater") || (this.termList[i] == "larger") || (this.termList[i] == "bigger")) {
          // check for one number
          var numberPattern = /\d+/g;
          var numbers = this.inputText.value.match( numberPattern );
          var startValue, endValue, matches, correctRange;

          // 'between' query
          if ((numbers != undefined) && (numbers.length == 1)) {
            startValue = Number(numbers[0]) + 1;

            for (var j = 0; j < this.termList.length; j++) {
              if (this.termList[j] != 'to' && this.termList[j] != 'and' && this.termList[j] != 'between' && this.termList[j] != 'find' && this.termList[j] != 'show' && this.termList[j] != 'highlight') {
                var results = fuzzy.filter(this.termList[j], barObj.dataAttributes);
                //var options = { pre: '<', post: '>' };
                if (results.length > 0)
                  matches = results.map(function(el) { return el.string; });
              }
            }
            if (matches.length > 0) {
              correctRange = barObj.getRangeForAttr(matches[0]);
              endValue = correctRange[1];
              var inRange1 = barObj.checkMinRange(matches[0], Number(startValue));
              msg.push(' ' + "<select onchange='main.changeHighlightBy(this," + startValue + "," + endValue + ")'><option value='" + matches[0] + "' selected>" + matches[0] + "</option>");
              for (var j = 1; j < matches.length; j++) {

                msg.push("<option value='" + matches[j] + "'>" + matches[j] + "</option>");
              }
              msg.push("</select> between " + startValue + " and " + endValue);
              if (inRange1) {
                var result = barObj.highlightValuesBetween(matches[0], Number(startValue), Number(endValue));
              }
              else if ((!inRange1) || (!result)){

                errorMsg = 'No results found for that range. Try a range between ' + correctRange[0] + ' and ' + correctRange[1] + '.'
              }

            }

            else {
              errorMsg = 'No such data attribute or value exists.'
            }
          }
        }
        else if ((this.termList[i] == "lesser") || (this.termList[i] == "less") || (this.termList[i] == "smaller")) {
          // check for one number
          var numberPattern = /\d+/g;
          var numbers = this.inputText.value.match( numberPattern );
          var startValue, endValue, matches, correctRange;

          if ((numbers != undefined) && (numbers.length == 1)) {
            endValue = Number(numbers[0]) - 1;

            for (var j = 0; j < this.termList.length; j++) {
              if (this.termList[j] != 'to' && this.termList[j] != 'and' && this.termList[j] != 'between' && this.termList[j] != 'find' && this.termList[j] != 'show' && this.termList[j] != 'highlight') {
                var results = fuzzy.filter(this.termList[j], barObj.dataAttributes);
                //	var options = { pre: '<', post: '>' };
                if (results.length > 0)
                  matches = results.map(function(el) { return el.string; });
              }
            }
            if (matches.length > 0) {
              correctRange = barObj.getRangeForAttr(matches[0]);
              startValue = correctRange[0];
              var inRange1 = barObj.checkMaxRange(matches[0], Number(endValue));
              msg.push(' ' + "<select onchange='main.changeHighlightBy(this," + startValue + "," + endValue + ")'><option value='" + matches[0] + "' selected>" + matches[0] + "</option>");
              for (var j = 1; j < matches.length; j++) {

                msg.push("<option value='" + matches[j] + "'>" + matches[j] + "</option>");
              }
              msg.push("</select> between " + startValue + " and " + endValue);
              if (inRange1) {
                var result = barObj.highlightValuesBetween(matches[0], Number(startValue), Number(endValue));
              }
              else if ((!inRange1) || (!result)){

                errorMsg = 'No results found for that range. Try a range between ' + correctRange[0] + ' and ' + correctRange[1] + '.'
              }
            }
            else {
              errorMsg = 'No such data attribute or value exists.'
            }
          }

        }



        break;
      case 'reset':
      case 'all':
        msg.push(this.termList[i]);
        barObj.initializeBarChart();
        break;
      default:
        if (this.termList.indexOf("sort") == -1) {
          // to do: support BOTH max and min in the same query.
          if ((this.termList.indexOf("max") > -1) || (this.termList.indexOf("most") > -1) || (this.termList.indexOf("maximum") > -1) || (this.termList.indexOf("high") > -1) || (this.termList.indexOf("highest") > -1) || (this.termList.indexOf("largest") > -1) || (this.termList.indexOf("biggest") > -1)) {
            barObj.max();

          }
          if ((this.termList.indexOf("min") > -1) || (this.termList.indexOf("low") > -1) || (this.termList.indexOf("minimum") > -1) || (this.termList.indexOf("lowest") > -1) || (this.termList.indexOf("smallest") > -1) || (this.termList.indexOf("least") > -1)) {
            barObj.min();

          }
        }
        //msg.push(this.termList[i]);
        break;



    }
  }
  main.errorContainer.innerHTML = errorMsg;
  //final period at end of parsed msg
  msg.push('.');

  main.outputContainer.innerHTML = msg.join(' ');


}
function showSpinner(){
  var infoMsg = '<i class="fas fa-spinner fa-spin" style="font-size:24px;color:#1a0dab" aria-hidden="true"></i>';
  $('.errorContainer').html(infoMsg);
  //console.warn('Spin');

}
vizta.Main.prototype.processDashboardNlpResults  = function(callback){
  showSpinner();
  setTimeout(function() {
    main.parserObj.debugParse2(main.parserObj.currentTree);
    var entries = [];


    if(main.parserObj.currentTree && main.parserObj.currentTree.hasError){
      console.log('parse tree has err');

      //console.log(main.parserObj.currentTree);
      var lexicons = main.parserObj.currentTree.parser.lexicons._lexicons;
      var candiateAttributes = main.visMgr.getAllAttributes();
      for (var i = 0; i < lexicons.length; i++) {
        for (var j = 0; j < lexicons[i].entries.length; j++) {

          if (lexicons[i].entries[j].dataset === main.datasource) {
            for(var k=0;k< candiateAttributes.length; k++){
              if(lexicons[i].entries[j].attribute){
                if(lexicons[i].entries[j].attribute.key === candiateAttributes[k]
                    && lexicons[i].entries[j].attribute.dataType==="categorical"){
                  entries.push(lexicons[i].entries[j]);
                  break;
                }
              }
              else{
                entries.push(lexicons[i].entries[j]);
              }
            }

          }
          if(!lexicons[i].entries[j].dataset){ // dataset agnostic terms
            entries.push(lexicons[i].entries[j]);
          }

        }
      }



      var fuse = new Fuse(entries, { keys: ['term'], include: ['score', 'matches'], threshold: .2  });

      main.suggestedTerms = [];
      for(var i=0;i<main.termList.length;i++){
        main.suggestedTerms.push(main.termList[i]);
        //console.log(main.termList[i]);
      }
      //console.log(main.termList);
      for(var i=0;i<main.parserObj.errorTerms.length;i++){

        var results = fuse.search(main.parserObj.errorTerms[i]);
        if(results.length>0){
          if(results[0].score === 0){
            console.log('perfect matched');
          }

          else{
            for(var j=0; j<main.suggestedTerms.length;j++){
              if(main.parserObj.errorTerms[i] === main.suggestedTerms[j].toLowerCase()){
                if(results[0].item.term.length<main.parserObj.errorTerms[i].length+2){

                  main.suggestedTerms[j] =  results[0].item.term;

                }
              }
            }
          }
        }
        else{
          //  main.errorContainer.innerHTML =  "The system doesn't understand the query. Try a different one.";

          break;
        }
      }


      var correctedText = main.suggestedTerms.join(' ');
      main.runAutoCorrectQuery = true;
      runParser(correctedText, function() {

        if(main.dashboardController.currentQuery !="" ){

        }
      });


    }
    else{
      // console.log("main.parserObj:",main.parserObj)
      // console.log("main.parserObj.currentTree:",main.parserObj.currentTree)
      main.dashboardController.currentQuery = main.parserObj.currentTree.children[0].value;
      main.dashboardController.currentQueryType = main.parserObj.currentTree.children[0].constructor.name;
      main.dashboardController.queryWidgets = main.parserObj.currentTree.children[0].queryWidgets;
      if(main.dashboardController.currentQuery !="" || main.dashboardController.currentQuery !=[]){
        // if(!main.runAutoCorrectQuery){
        //   main.errorContainer.innerHTML =  "";
        // }

        main.dashboardController.updateDashboard(main.termList, true);
        main.dashboardController.previousQuery = main.dashboardController.currentQuery;

      }
      else {
        main.errorContainer.innerHTML =  "This query is not currently supported. Try a different one.";
      }

    }
    //$('#LoadingImage').hide();
    callback()
  }, 100);

};

vizta.Main.prototype.getAttribute = function(attributeName){

  var dataset = main.getCurrentDataset();
  //console.log(dataset);

  for (var i = 0; i < dataset.attributes.length; i++) {
    if(dataset.attributes[i].key===attributeName){
      return dataset.attributes[i];
    }
  }
  return null;
};

vizta.Main.prototype.getCurrentDataset = function (){
  var datasetName = main.datasource;
  //console.log(main.currentDataset);

  //var dataset = lexicons.getDataset(datasetName);
  //main.currentDataset = dataset;
  return main.currentDataset;
  /*if(main.parserObj.currentTree){
    var datasets = main.parserObj.currentTree.parser.lexicons.datasets;
    for(var i=0; i< datasets.length; i++){
      if(datasets[i].name === main.datasource){
        return datasets[i];
      }
    }
  }*/
}
function runCorrectedQuery( ) {
  //main.errorContainer.innerHTML =  "";
  main.inputText.value = main.suggestedTerms.join(' ');

  //main.dashboardController.currentQuery = main.suggestedTerms.join(' ');
  //main.dashboardController.updateDashboard(main.termList, true);

}

function runParser(text, callback) {
  var parseOptions = {originalInput: text, skipAutocomplete: true};
  main.parserObj.parse(text, parseOptions);
  callback();
}

function getAttribute(start, end, terms) {
  var attr = '';
  attr = terms.match(start + "(.*?)" + end);
  return attr;
}


//
function checkNearEntity(i, terms)
{
  var entity;

  if ((i*1 + 1) <= (terms.length -1))
  {
    //entity = terms[i*1+1];
    entity = terms.slice(i*1+1).join(' ');
  }
  return entity;

}


//
function checkYearEntity(terms, years)
{
  var yearlist = [];

  for (var i = 0; i < terms.length; i++)
  {
    if (years.indexOf(terms[i]) != -1) {
      yearlist.push(terms[i]);
    }
  }
  return yearlist;


}

function getRange(terms)
{

  var numberPattern = /\d+/g;
  var min = 0;
  var max = 10;
  var numbers = new Array();
  for (var i = 0; i < terms.length; i++)
  {
    if (terms[i].match(numberPattern))
    {
      numbers.push(terms[i]);

    }


  }
  if (numbers.length == 2)
    return (numbers);
  else
    return ([min, max]);
}


function getNumber(terms)
{

  var numberPattern = /\d+/g;
  var min = 0;
  var max = 10;
  var number = new Array();
  for (var i = 0; i < terms.length; i++)
  {
    if (terms[i].match(numberPattern))
    {
      number.push(terms[i]);

    }


  }
  if (number.length == 1)
    return number[0];
  else
    return 5;
}






function toTitleCase(str)
{
  return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
}


function upDate(value) {
  var lineFn;
  if (main.datasource == 'temperatures') {
    lineFn = lineObj;

  }
  else if (main.datasource == 'stock prices') {
    lineFn = stocklineObj;

  }

  var dateFields = value.split("/");
  var monthNum = dateFields[0]-1;
  var month = lineFn.months[monthNum];
  var year = dateFields[2];
  var errorMsg = '';
  main.errorContainer.innerHTML = errorMsg;
  if (year != lineFn.year) {
    errorMsg =  '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No data exists for this year. Try ' + lineFn.year + '.';
    main.errorContainer.innerHTML = errorMsg;
  }

  else if (main.month != month) {
    main.month = month;

    // call semanticzoom on line chart with updated values
    var mainMonthNumber = Date.getMonthNumberFromName(main.month);
    var mainMonthDays = Date.getDaysInMonth(lineFn.year, mainMonthNumber);
    mainMonthNumber = mainMonthNumber + 1;


    var startDate = "01/" + mainMonthNumber  + "/" + lineFn.year;
    var endDate =  mainMonthDays + "/" + mainMonthNumber + "/" + lineFn.year;
    lineFn.semanticZoom(startDate, endDate);
  }
}


function upDateRange1(value) {
  var lineFn;
  if (main.datasource == 'temperatures') {
    lineFn = lineObj;

  }
  else if (main.datasource == 'stock prices') {
    lineFn = stocklineObj;

  }
  var dateFields = value.split("/");
  var startMonthNum = dateFields[0]-1;
  var startMonth = lineFn.months[startMonthNum];

  var year = dateFields[2];
  var errorMsg = '';
  main.errorContainer.innerHTML = errorMsg;
  if (year != lineFn.year) {
    errorMsg =  '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No data exists for this year. Try ' + lineFn.year + '.';
    main.errorContainer.innerHTML = errorMsg;
  }

  else {

    // call semanticzoom on line chart with updated values
    var startDate = "01/" + (startMonthNum + 1) + "/" + lineFn.year;

    // get enddate value from rangepicker2
    main.rp2 = document.getElementById('rangepicker2');
    var endDate1 = main.rp2.value;

    // get endMonth
    dateFields = endDate1.split("/");
    var endMonthNum = dateFields[0]-1;
    var endMonth = lineFn.months[endMonthNum];

    var endDate = "01/" + (endMonthNum + 1) + "/" + lineFn.year;

    if (startMonthNum >= endMonthNum) {
      errorMsg =  '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Start date must be before the end date.';
      main.errorContainer.innerHTML = errorMsg;

    }
    else {

      lineFn.semanticZoom(startDate, endDate);

    }




  }


}

function upDateRange2(value) {
  var lineFn;

  if (main.datasource == 'temperatures') {
    lineFn = lineObj;

  }
  else if (main.datasource == 'stock prices') {
    lineFn = stocklineObj;

  }
  var dateFields = value.split("/");
  var endMonthNum = dateFields[0]-1;
  var endMonth = lineFn.months[endMonthNum];

  var year = dateFields[2];
  var errorMsg = '';
  main.errorContainer.innerHTML = errorMsg;
  if (year != lineFn.year) {
    errorMsg =  '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No data exists for this year. Try ' + lineFn.year + '.';
    main.errorContainer.innerHTML = errorMsg;
  }

  else {

    // call semanticzoom on line chart with updated values
    var endDate = "01/" + (endMonthNum + 1) + "/" + lineFn.year;

    // get enddate value from rangepicker1
    var rp1 = document.getElementById('rangepicker1');
    var startDate1 = main.rp1.value;

    // get startMonth
    dateFields = startDate1.split("/");
    var startMonthNum = dateFields[0]-1;
    var startMonth = lineFn.months[startMonthNum];

    var startDate = "01/" + (startMonthNum + 1) + "/" + lineFn.year;

    if (endMonthNum <= startMonthNum) {
      errorMsg =  '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;End date must be after the start date.';
      main.errorContainer.innerHTML = errorMsg;

    }
    else {

      lineFn.semanticZoom(startDate, endDate);

    }




  }


}

vizta.Main.prototype.changeSeasonFunc = function(obj) {
  var lineFn, seasonRange;
  var value = obj.value;
  if (main.datasource == 'temperatures') {
    lineFn = lineObj;

  }
  else if (main.datasource == 'stock prices') {
    lineFn = stocklineObj;
  }
  lineFn.hemisphere = value;

  // get value from drop-down and season
  if (value == 'Northern Hemisphere') {
    seasonRange = lineFn.seasonRanges[lineFn.season];
  }
  else {
    seasonRange = lineFn.southSeasonRanges[lineFn.season];
  }

  // Parse the dates
  var dates = seasonRange.split('*');
  var startDate = dates[0] +  "/" + lineFn.year;

  if ((lineFn.season == 'winter' && lineFn.hemisphere == 'Northern Hemisphere')  || (lineFn.season == 'summer' && lineFn.hemisphere == 'Southern Hemisphere')) {
    var endDate = dates[1] + "/" + (lineFn.year + 1).toString();
  }
  else {
    endDate = dates[1] + "/" + lineFn.year;
  }
  lineFn.semanticZoom(startDate, endDate);

  var d = dates[0].split("/");
  var datePickerStartDate = d[1] + "/" + d[0] + "/" + lineFn.year;
  var datePickerEndDate;
  d = dates[1].split("/");
  if ((lineFn.season == 'winter' && lineFn.hemisphere == 'Northern Hemisphere')  || (lineFn.season == 'summer' && lineFn.hemisphere == 'Southern Hemisphere')) {
    datePickerEndDate = d[1] + "/" + d[0] + "/" + (lineFn.year + 1).toString();
  }
  else {
    datePickerEndDate = d[1] + "/" + d[0] + "/" + lineFn.year;
  }

  // update seasonrange1 and seasonrange2

  this.rp1.setDate(datePickerStartDate);
  this.rp2.setDate(datePickerEndDate);



}



function upDateSeasonRange1(value) {
  var lineFn;
  if (main.datasource == 'temperatures') {
    lineFn = lineObj;

  }
  else if (main.datasource == 'stock prices') {
    lineFn = stocklineObj;

  }
  var dateFields = value.split("/");
  var startDate = dateFields[1];
  var startMonthNum = dateFields[0]-1;
  var startMonth = lineFn.months[startMonthNum];

  var year = dateFields[2];
  var errorMsg = '';
  main.errorContainer.innerHTML = errorMsg;
  if (year != lineFn.year) {
    errorMsg =  '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No data exists for this year. Try ' + lineFn.year + '.';
    main.errorContainer.innerHTML = errorMsg;
  }

  else {

    // call semanticzoom on line chart with updated values
    var startDate = startDate + "/" + (startMonthNum + 1) + "/" + lineFn.year;

    // get enddate value from rangepicker2
    var rp2 = document.getElementById('rangepicker2');
    var endDate1 = rp2.value;

    // get endMonth
    dateFields = endDate1.split("/");
    var endDate = dateFields[1];
    var endMonthNum = dateFields[0]-1;
    var endMonth = lineFn.months[endMonthNum];
    var endDate;
    if ((lineFn.season == 'winter' && lineFn.hemisphere == 'Northern Hemisphere')  || (lineFn.season == 'summer' && lineFn.hemisphere == 'Southern Hemisphere')) {
      endDate = endDate + "/" + (endMonthNum + 1) + "/" + (lineFn.year + 1).toString();
      lineFn.semanticZoom(startDate, endDate);
    }
    else {
      endDate = endDate + "/" + (endMonthNum + 1) + "/" + (lineFn.year + 1).toString();
      if (startMonthNum >= endMonthNum) {
        errorMsg =  '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Start date must be before the end date.';
        main.errorContainer.innerHTML = errorMsg;

      }
      else {
        lineFn.semanticZoom(startDate, endDate);
      }

    }




  }


}


function upDateSeasonRange2(value) {
  var lineFn;

  if (main.datasource == 'temperatures') {
    lineFn = lineObj;

  }
  else if (main.datasource == 'stock prices') {
    lineFn = stocklineObj;

  }
  var dateFields = value.split("/");
  var endDate = dateFields[1];
  var endMonthNum = dateFields[0]-1;
  var endMonth = lineFn.months[endMonthNum];

  var year = dateFields[2];
  var errorMsg = '';
  main.errorContainer.innerHTML = errorMsg;
  if ((lineFn.season == 'winter' && lineFn.hemisphere == 'Northern Hemisphere')  || (lineFn.season == 'summer' && lineFn.hemisphere == 'Southern Hemisphere')) {

  }
  else if (year != lineFn.year) {
    errorMsg =  '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;No data exists for this year. Try ' + lineFn.year + '.';
    main.errorContainer.innerHTML = errorMsg;
  }

  else {

    // call semanticzoom on line chart with updated values
    var endDate;
    if ((lineFn.season == 'winter' && lineFn.hemisphere == 'Northern Hemisphere')  || (lineFn.season == 'summer' && lineFn.hemisphere == 'Southern Hemisphere')) {
      endDate = endDate + "/" + (endMonthNum + 1) + "/" + (lineFn.year + 1).toString();
    }
    else {
      endDate = endDate + "/" + (endMonthNum + 1) + "/" + lineFn.year;
    }

    // get enddate value from rangepicker1
    var rp1 = document.getElementById('rangepicker1');
    var startDate1 = rp1.value;

    // get startMonth
    dateFields = startDate1.split("/");
    var startDate = dateFields[1];
    var startMonthNum = dateFields[0]-1;
    var startMonth = lineFn.months[startMonthNum];

    var startDate = startDate + "/" + (startMonthNum + 1) + "/" + lineFn.year;
    if ((lineFn.season == 'winter' && lineFn.hemisphere == 'Northern Hemisphere')  || (lineFn.season == 'summer' && lineFn.hemisphere == 'Southern Hemisphere')) {
      lineFn.semanticZoom(startDate, endDate);
    }
    else if (endMonthNum <= startMonthNum) {
      errorMsg =  '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;End date must be after the start date.';
      main.errorContainer.innerHTML = errorMsg;

    }
    else {

      lineFn.semanticZoom(startDate, endDate);

    }




  }


}


function hideFactorGraph(){
  if ($("#showfactorgraph").prop("checked")){
    $("#FactorGraphDisplay")[0].style.visibility="visible"
  }else{
    $("#FactorGraphDisplay")[0].style.visibility="hidden"
  }
}

vizta.Main.prototype.updateMap = function(newMagnitude)
{
  var errorMsg = '';
  var result;
  main.errorContainer.innerHTML = errorMsg;


  if (this.size == 'large') {
    this.minLargeMag = newMagnitude;

  }
  if (this.termList.indexOf("near") == -1 && main.place == '') {
    result = mapObj.select_by_Qattribute("magnitude", newMagnitude, 10);

  }
  else {

    result = mapObj.select_by_Qattribute_state(this.place.toLowerCase(), 100, "magnitude", newMagnitude, 10);
    mapObj.add_tooltip('selected', ['magnitude', 'Magnitude'], ['earthquake_datetime', 'Date']);
    mapObj.encode_size('selected', 'magnitude', 1.2);

  }
  if (!result) {
    errorMsg = 'There were no marks selected that meet these criteria. Want to modify your query?';
  }
  else {
    errorMsg = '';
  }

  main.errorContainer.innerHTML = errorMsg;
}



vizta.Main.prototype.updateMapDistance = function(newDistance)
{
  var errorMsg = '';
  var result;
  main.errorContainer.innerHTML = errorMsg;
  if (this.termList.indexOf("near") != -1) {

    result = mapObj.select_by_Qattribute_state(this.place.toLowerCase(), newDistance, "magnitude", this.min, this.max);

  }
  else if (this.termList.indexOf("outside") != -1){
    result = mapObj.select_outside_Qattribute_state(this.place.toLowerCase(), newDistance, "magnitude", this.min, this.max);

  }
  if (!result) {
    errorMsg = 'There were no marks selected that meet these criteria. Want to modify your query?';
  }
  else {
    errorMsg = '';
  }
  main.errorContainer.innerHTML = errorMsg;
}




/**
 * TODO: clean this up
 */
function formatTree(data, output, index) {
  output += '<br>\n';
  var y = 0;
  while (y < index) {
    output += '&nbsp;&nbsp;'; y += 1;
  }

  output += '{ \'value\':\'' + data.value + '\', \'id\':\'' + data.id + '\'';
  output += ', \'children\': [';
  if (data.children.length > 0) {
    index += 1;

    var x = 0;
    while (x < data.children.length) {
      output = formatTree(data.children[x], output, index);
      x += 1;
    }
  }

  output += ']';
  output += '}';

  return output;
}

// get the intersection of two arrays, need to move this to utils...
function getIntersect(arr1, arr2) {
  var r = [], o = {}, l = arr2.length, i, v;
  for (i = 0; i < l; i++) {
    o[arr2[i]] = true;
  }
  l = arr1.length;
  for (i = 0; i < l; i++) {
    v = arr1[i];
    if (v in o) {
      r.push(v);
    }
  }
  return r;
}

// start the app once the HTML dom has loaded
var main;

window.addEventListener('load', function (callback) {
    // console.log("before Eviza Main")
    main = new vizta.Main();
    // console.log("after Eviza Main")
}, false)
// $(document).load(function(){
//   main.initViz();
// })
// .success(function(){main.initViz();})
