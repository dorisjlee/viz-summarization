
console.log(nodeid);
console.log(svglist);
console.log(edgelist);
var network = null;

var DIR = 'img/refresh-cl/';
var LENGTH_MAIN = 150;
var LENGTH_SUB = 50;

// Called when the Visualization API is loaded.
function draw(node,edge) {
    if (typeof(node)=="string"){
      nodelist = node.replace("[","").replace("]","");
      nodelist = nodelist.trim();
      nodelist = nodelist.split(',');
      for(var i=0; i<nodelist.length; i++) { nodelist[i] = nodelist[i].trim(); }
    }else{
      nodelist = node;
    }
    if (typeof(edge)=="string"){
      edgelist = edge.replace("[","").replace("]","");
      edgelist = edgelist.split(',').map(function(item) {
          return parseInt(item, 10);
      });      
    }else{
      edgelist = edge;
    }
    
    nodeid = []
    svglist = []
    for(var i=0; i<nodelist.length; i=i+2) { nodeid.push(+nodelist[i]); }
    for(var i=1; i<nodelist.length; i=i+2) { svglist.push(nodelist[i].slice(1,-1)); }
    
    nodes = [];
    edges = [];
    for(var i=0; i<nodeid.length; i++) {
        //a = '<?xml version="1.0" encoding="utf-8" standalone="no"?>\n<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN"\n  "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">\n<!-- Created with matplotlib (http://matplotlib.org/) -->\n<svg height="144pt" version="1.1" viewBox="0 0 144 144" width="144pt" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">\n <defs>\n  <style type="text/css">\n*{stroke-linecap:butt;stroke-linejoin:round;}\n  </style>\n </defs>\n <g id="figure_1">\n  <g id="patch_1">\n   <path d="\nM0 144\nL144 144\nL144 0\nL0 0\nz\n" style="fill:#ffffff;"/>\n  </g>\n  <g id="axes_1">\n   <g id="patch_2">\n    <path d="\nM56.2303 95.94\nL131.04 95.94\nL131.04 19.26\nL56.2303 19.26\nz\n" style="fill:#ffffff;"/>\n   </g>\n   <g id="patch_3">\n    <path clip-path="url(#p7b8e183f0b)" d="\nM68.6986 95.94\nL93.6352 95.94\nL93.6352 42.264\nL68.6986 42.264\nz\n" style="fill:#ff0000;stroke:#000000;"/>\n   </g>\n   <g id="patch_4">\n    <path clip-path="url(#p7b8e183f0b)" d="\nM93.6352 95.94\nL118.572 95.94\nL118.572 72.936\nL93.6352 72.936\nz\n" style="fill:#00fff3;stroke:#000000;"/>\n   </g>\n   <g id="matplotlib.axis_1">\n    <g id="xtick_1">\n     <g id="line2d_1">\n      <defs>\n       <path d="\nM0 0\nL0 -4" id="m93b0483c22" style="stroke:#000000;stroke-width:0.5;"/>\n      </defs>\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="81.7902890625" xlink:href="#m93b0483c22" y="95.94"/>\n      </g>\n     </g>\n     <g id="line2d_2">\n      <defs>\n       <path d="\nM0 0\nL0 4" id="m741efc42ff" style="stroke:#000000;stroke-width:0.5;"/>\n      </defs>\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="81.7902890625" xlink:href="#m741efc42ff" y="19.26"/>\n      </g>\n     </g>\n     <g id="text_1">\n      <!-- M -->\n      <defs>\n       <path d="\nM9.8125 72.9062\nL24.5156 72.9062\nL43.1094 23.2969\nL61.8125 72.9062\nL76.5156 72.9062\nL76.5156 0\nL66.8906 0\nL66.8906 64.0156\nL48.0938 14.0156\nL38.1875 14.0156\nL19.3906 64.0156\nL19.3906 0\nL9.8125 0\nz\n" id="BitstreamVeraSans-Roman-4d"/>\n      </defs>\n      <g transform="translate(77.7881015625 109.058125)scale(0.12 -0.12)">\n       <use xlink:href="#BitstreamVeraSans-Roman-4d"/>\n      </g>\n     </g>\n    </g>\n    <g id="xtick_2">\n     <g id="line2d_3">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="100.492710938" xlink:href="#m93b0483c22" y="95.94"/>\n      </g>\n     </g>\n     <g id="line2d_4">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="100.492710938" xlink:href="#m741efc42ff" y="19.26"/>\n      </g>\n     </g>\n     <g id="text_2">\n      <!-- F -->\n      <defs>\n       <path d="\nM9.8125 72.9062\nL51.7031 72.9062\nL51.7031 64.5938\nL19.6719 64.5938\nL19.6719 43.1094\nL48.5781 43.1094\nL48.5781 34.8125\nL19.6719 34.8125\nL19.6719 0\nL9.8125 0\nz\n" id="BitstreamVeraSans-Roman-46"/>\n      </defs>\n      <g transform="translate(97.9792734375 109.058125)scale(0.12 -0.12)">\n       <use xlink:href="#BitstreamVeraSans-Roman-46"/>\n      </g>\n     </g>\n    </g>\n    <g id="text_3">\n     <!-- Gender -->\n     <defs>\n      <path d="\nM59.5156 10.4062\nL59.5156 29.9844\nL43.4062 29.9844\nL43.4062 38.0938\nL69.2812 38.0938\nL69.2812 6.78125\nQ63.5781 2.73438 56.6875 0.65625\nQ49.8125 -1.42188 42 -1.42188\nQ24.9062 -1.42188 15.25 8.5625\nQ5.60938 18.5625 5.60938 36.375\nQ5.60938 54.25 15.25 64.2344\nQ24.9062 74.2188 42 74.2188\nQ49.125 74.2188 55.5469 72.4531\nQ61.9688 70.7031 67.3906 67.2812\nL67.3906 56.7812\nQ61.9219 61.4219 55.7656 63.7656\nQ49.6094 66.1094 42.8281 66.1094\nQ29.4375 66.1094 22.7188 58.6406\nQ16.0156 51.1719 16.0156 36.375\nQ16.0156 21.625 22.7188 14.1562\nQ29.4375 6.6875 42.8281 6.6875\nQ48.0469 6.6875 52.1406 7.59375\nQ56.25 8.5 59.5156 10.4062" id="BitstreamVeraSans-Roman-47"/>\n      <path d="\nM56.2031 29.5938\nL56.2031 25.2031\nL14.8906 25.2031\nQ15.4844 15.9219 20.4844 11.0625\nQ25.4844 6.20312 34.4219 6.20312\nQ39.5938 6.20312 44.4531 7.46875\nQ49.3125 8.73438 54.1094 11.2812\nL54.1094 2.78125\nQ49.2656 0.734375 44.1875 -0.34375\nQ39.1094 -1.42188 33.8906 -1.42188\nQ20.7969 -1.42188 13.1562 6.1875\nQ5.51562 13.8125 5.51562 26.8125\nQ5.51562 40.2344 12.7656 48.1094\nQ20.0156 56 32.3281 56\nQ43.3594 56 49.7812 48.8906\nQ56.2031 41.7969 56.2031 29.5938\nM47.2188 32.2344\nQ47.125 39.5938 43.0938 43.9844\nQ39.0625 48.3906 32.4219 48.3906\nQ24.9062 48.3906 20.3906 44.1406\nQ15.875 39.8906 15.1875 32.1719\nz\n" id="BitstreamVeraSans-Roman-65"/>\n      <path d="\nM54.8906 33.0156\nL54.8906 0\nL45.9062 0\nL45.9062 32.7188\nQ45.9062 40.4844 42.875 44.3281\nQ39.8438 48.1875 33.7969 48.1875\nQ26.5156 48.1875 22.3125 43.5469\nQ18.1094 38.9219 18.1094 30.9062\nL18.1094 0\nL9.07812 0\nL9.07812 54.6875\nL18.1094 54.6875\nL18.1094 46.1875\nQ21.3438 51.125 25.7031 53.5625\nQ30.0781 56 35.7969 56\nQ45.2188 56 50.0469 50.1719\nQ54.8906 44.3438 54.8906 33.0156" id="BitstreamVeraSans-Roman-6e"/>\n      <path d="\nM41.1094 46.2969\nQ39.5938 47.1719 37.8125 47.5781\nQ36.0312 48 33.8906 48\nQ26.2656 48 22.1875 43.0469\nQ18.1094 38.0938 18.1094 28.8125\nL18.1094 0\nL9.07812 0\nL9.07812 54.6875\nL18.1094 54.6875\nL18.1094 46.1875\nQ20.9531 51.1719 25.4844 53.5781\nQ30.0312 56 36.5312 56\nQ37.4531 56 38.5781 55.875\nQ39.7031 55.7656 41.0625 55.5156\nz\n" id="BitstreamVeraSans-Roman-72"/>\n      <path d="\nM45.4062 46.3906\nL45.4062 75.9844\nL54.3906 75.9844\nL54.3906 0\nL45.4062 0\nL45.4062 8.20312\nQ42.5781 3.32812 38.25 0.953125\nQ33.9375 -1.42188 27.875 -1.42188\nQ17.9688 -1.42188 11.7344 6.48438\nQ5.51562 14.4062 5.51562 27.2969\nQ5.51562 40.1875 11.7344 48.0938\nQ17.9688 56 27.875 56\nQ33.9375 56 38.25 53.625\nQ42.5781 51.2656 45.4062 46.3906\nM14.7969 27.2969\nQ14.7969 17.3906 18.875 11.75\nQ22.9531 6.10938 30.0781 6.10938\nQ37.2031 6.10938 41.2969 11.75\nQ45.4062 17.3906 45.4062 27.2969\nQ45.4062 37.2031 41.2969 42.8438\nQ37.2031 48.4844 30.0781 48.4844\nQ22.9531 48.4844 18.875 42.8438\nQ14.7969 37.2031 14.7969 27.2969" id="BitstreamVeraSans-Roman-64"/>\n     </defs>\n     <g transform="translate(68.23171875 127.1915625)scale(0.14 -0.14)">\n      <use xlink:href="#BitstreamVeraSans-Roman-47"/>\n      <use x="77.490234375" xlink:href="#BitstreamVeraSans-Roman-65"/>\n      <use x="139.013671875" xlink:href="#BitstreamVeraSans-Roman-6e"/>\n      <use x="202.392578125" xlink:href="#BitstreamVeraSans-Roman-64"/>\n      <use x="265.869140625" xlink:href="#BitstreamVeraSans-Roman-65"/>\n      <use x="327.392578125" xlink:href="#BitstreamVeraSans-Roman-72"/>\n     </g>\n    </g>\n   </g>\n   <g id="matplotlib.axis_2">\n    <g id="ytick_1">\n     <g id="line2d_5">\n      <defs>\n       <path d="\nM0 0\nL4 0" id="m728421d6d4" style="stroke:#000000;stroke-width:0.5;"/>\n      </defs>\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="56.2303125" xlink:href="#m728421d6d4" y="95.94"/>\n      </g>\n     </g>\n     <g id="line2d_6">\n      <defs>\n       <path d="\nM0 0\nL-4 0" id="mcb0005524f" style="stroke:#000000;stroke-width:0.5;"/>\n      </defs>\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="131.04" xlink:href="#mcb0005524f" y="95.94"/>\n      </g>\n     </g>\n     <g id="text_4">\n      <!-- 0 -->\n      <defs>\n       <path d="\nM31.7812 66.4062\nQ24.1719 66.4062 20.3281 58.9062\nQ16.5 51.4219 16.5 36.375\nQ16.5 21.3906 20.3281 13.8906\nQ24.1719 6.39062 31.7812 6.39062\nQ39.4531 6.39062 43.2812 13.8906\nQ47.125 21.3906 47.125 36.375\nQ47.125 51.4219 43.2812 58.9062\nQ39.4531 66.4062 31.7812 66.4062\nM31.7812 74.2188\nQ44.0469 74.2188 50.5156 64.5156\nQ56.9844 54.8281 56.9844 36.375\nQ56.9844 17.9688 50.5156 8.26562\nQ44.0469 -1.42188 31.7812 -1.42188\nQ19.5312 -1.42188 13.0625 8.26562\nQ6.59375 17.9688 6.59375 36.375\nQ6.59375 54.8281 13.0625 64.5156\nQ19.5312 74.2188 31.7812 74.2188" id="BitstreamVeraSans-Roman-30"/>\n      </defs>\n      <g transform="translate(46.1834375 99.25125)scale(0.12 -0.12)">\n       <use xlink:href="#BitstreamVeraSans-Roman-30"/>\n      </g>\n     </g>\n    </g>\n    <g id="ytick_2">\n     <g id="line2d_7">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="56.2303125" xlink:href="#m728421d6d4" y="80.604"/>\n      </g>\n     </g>\n     <g id="line2d_8">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="131.04" xlink:href="#mcb0005524f" y="80.604"/>\n      </g>\n     </g>\n     <g id="text_5">\n      <!-- 20 -->\n      <defs>\n       <path d="\nM19.1875 8.29688\nL53.6094 8.29688\nL53.6094 0\nL7.32812 0\nL7.32812 8.29688\nQ12.9375 14.1094 22.625 23.8906\nQ32.3281 33.6875 34.8125 36.5312\nQ39.5469 41.8438 41.4219 45.5312\nQ43.3125 49.2188 43.3125 52.7812\nQ43.3125 58.5938 39.2344 62.25\nQ35.1562 65.9219 28.6094 65.9219\nQ23.9688 65.9219 18.8125 64.3125\nQ13.6719 62.7031 7.8125 59.4219\nL7.8125 69.3906\nQ13.7656 71.7812 18.9375 73\nQ24.125 74.2188 28.4219 74.2188\nQ39.75 74.2188 46.4844 68.5469\nQ53.2188 62.8906 53.2188 53.4219\nQ53.2188 48.9219 51.5312 44.8906\nQ49.8594 40.875 45.4062 35.4062\nQ44.1875 33.9844 37.6406 27.2188\nQ31.1094 20.4531 19.1875 8.29688" id="BitstreamVeraSans-Roman-32"/>\n      </defs>\n      <g transform="translate(38.6365625 83.91525)scale(0.12 -0.12)">\n       <use xlink:href="#BitstreamVeraSans-Roman-32"/>\n       <use x="63.623046875" xlink:href="#BitstreamVeraSans-Roman-30"/>\n      </g>\n     </g>\n    </g>\n    <g id="ytick_3">\n     <g id="line2d_9">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="56.2303125" xlink:href="#m728421d6d4" y="65.268"/>\n      </g>\n     </g>\n     <g id="line2d_10">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="131.04" xlink:href="#mcb0005524f" y="65.268"/>\n      </g>\n     </g>\n     <g id="text_6">\n      <!-- 40 -->\n      <defs>\n       <path d="\nM37.7969 64.3125\nL12.8906 25.3906\nL37.7969 25.3906\nz\n\nM35.2031 72.9062\nL47.6094 72.9062\nL47.6094 25.3906\nL58.0156 25.3906\nL58.0156 17.1875\nL47.6094 17.1875\nL47.6094 0\nL37.7969 0\nL37.7969 17.1875\nL4.89062 17.1875\nL4.89062 26.7031\nz\n" id="BitstreamVeraSans-Roman-34"/>\n      </defs>\n      <g transform="translate(38.3440625 68.57925)scale(0.12 -0.12)">\n       <use xlink:href="#BitstreamVeraSans-Roman-34"/>\n       <use x="63.623046875" xlink:href="#BitstreamVeraSans-Roman-30"/>\n      </g>\n     </g>\n    </g>\n    <g id="ytick_4">\n     <g id="line2d_11">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="56.2303125" xlink:href="#m728421d6d4" y="49.932"/>\n      </g>\n     </g>\n     <g id="line2d_12">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="131.04" xlink:href="#mcb0005524f" y="49.932"/>\n      </g>\n     </g>\n     <g id="text_7">\n      <!-- 60 -->\n      <defs>\n       <path d="\nM33.0156 40.375\nQ26.375 40.375 22.4844 35.8281\nQ18.6094 31.2969 18.6094 23.3906\nQ18.6094 15.5312 22.4844 10.9531\nQ26.375 6.39062 33.0156 6.39062\nQ39.6562 6.39062 43.5312 10.9531\nQ47.4062 15.5312 47.4062 23.3906\nQ47.4062 31.2969 43.5312 35.8281\nQ39.6562 40.375 33.0156 40.375\nM52.5938 71.2969\nL52.5938 62.3125\nQ48.875 64.0625 45.0938 64.9844\nQ41.3125 65.9219 37.5938 65.9219\nQ27.8281 65.9219 22.6719 59.3281\nQ17.5312 52.7344 16.7969 39.4062\nQ19.6719 43.6562 24.0156 45.9219\nQ28.375 48.1875 33.5938 48.1875\nQ44.5781 48.1875 50.9531 41.5156\nQ57.3281 34.8594 57.3281 23.3906\nQ57.3281 12.1562 50.6875 5.35938\nQ44.0469 -1.42188 33.0156 -1.42188\nQ20.3594 -1.42188 13.6719 8.26562\nQ6.98438 17.9688 6.98438 36.375\nQ6.98438 53.6562 15.1875 63.9375\nQ23.3906 74.2188 37.2031 74.2188\nQ40.9219 74.2188 44.7031 73.4844\nQ48.4844 72.75 52.5938 71.2969" id="BitstreamVeraSans-Roman-36"/>\n      </defs>\n      <g transform="translate(38.5953125 53.24325)scale(0.12 -0.12)">\n       <use xlink:href="#BitstreamVeraSans-Roman-36"/>\n       <use x="63.623046875" xlink:href="#BitstreamVeraSans-Roman-30"/>\n      </g>\n     </g>\n    </g>\n    <g id="ytick_5">\n     <g id="line2d_13">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="56.2303125" xlink:href="#m728421d6d4" y="34.596"/>\n      </g>\n     </g>\n     <g id="line2d_14">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="131.04" xlink:href="#mcb0005524f" y="34.596"/>\n      </g>\n     </g>\n     <g id="text_8">\n      <!-- 80 -->\n      <defs>\n       <path d="\nM31.7812 34.625\nQ24.75 34.625 20.7188 30.8594\nQ16.7031 27.0938 16.7031 20.5156\nQ16.7031 13.9219 20.7188 10.1562\nQ24.75 6.39062 31.7812 6.39062\nQ38.8125 6.39062 42.8594 10.1719\nQ46.9219 13.9688 46.9219 20.5156\nQ46.9219 27.0938 42.8906 30.8594\nQ38.875 34.625 31.7812 34.625\nM21.9219 38.8125\nQ15.5781 40.375 12.0312 44.7188\nQ8.5 49.0781 8.5 55.3281\nQ8.5 64.0625 14.7188 69.1406\nQ20.9531 74.2188 31.7812 74.2188\nQ42.6719 74.2188 48.875 69.1406\nQ55.0781 64.0625 55.0781 55.3281\nQ55.0781 49.0781 51.5312 44.7188\nQ48 40.375 41.7031 38.8125\nQ48.8281 37.1562 52.7969 32.3125\nQ56.7812 27.4844 56.7812 20.5156\nQ56.7812 9.90625 50.3125 4.23438\nQ43.8438 -1.42188 31.7812 -1.42188\nQ19.7344 -1.42188 13.25 4.23438\nQ6.78125 9.90625 6.78125 20.5156\nQ6.78125 27.4844 10.7812 32.3125\nQ14.7969 37.1562 21.9219 38.8125\nM18.3125 54.3906\nQ18.3125 48.7344 21.8438 45.5625\nQ25.3906 42.3906 31.7812 42.3906\nQ38.1406 42.3906 41.7188 45.5625\nQ45.3125 48.7344 45.3125 54.3906\nQ45.3125 60.0625 41.7188 63.2344\nQ38.1406 66.4062 31.7812 66.4062\nQ25.3906 66.4062 21.8438 63.2344\nQ18.3125 60.0625 18.3125 54.3906" id="BitstreamVeraSans-Roman-38"/>\n      </defs>\n      <g transform="translate(38.5709375 37.90725)scale(0.12 -0.12)">\n       <use xlink:href="#BitstreamVeraSans-Roman-38"/>\n       <use x="63.623046875" xlink:href="#BitstreamVeraSans-Roman-30"/>\n      </g>\n     </g>\n    </g>\n    <g id="ytick_6">\n     <g id="line2d_15">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="56.2303125" xlink:href="#m728421d6d4" y="19.26"/>\n      </g>\n     </g>\n     <g id="line2d_16">\n      <g>\n       <use style="stroke:#000000;stroke-width:0.5;" x="131.04" xlink:href="#mcb0005524f" y="19.26"/>\n      </g>\n     </g>\n     <g id="text_9">\n      <!-- 100 -->\n      <defs>\n       <path d="\nM12.4062 8.29688\nL28.5156 8.29688\nL28.5156 63.9219\nL10.9844 60.4062\nL10.9844 69.3906\nL28.4219 72.9062\nL38.2812 72.9062\nL38.2812 8.29688\nL54.3906 8.29688\nL54.3906 0\nL12.4062 0\nz\n" id="BitstreamVeraSans-Roman-31"/>\n      </defs>\n      <g transform="translate(31.4403125 22.57125)scale(0.12 -0.12)">\n       <use xlink:href="#BitstreamVeraSans-Roman-31"/>\n       <use x="63.623046875" xlink:href="#BitstreamVeraSans-Roman-30"/>\n       <use x="127.24609375" xlink:href="#BitstreamVeraSans-Roman-30"/>\n      </g>\n     </g>\n    </g>\n    <g id="text_10">\n     <!-- COUNT(id) -->\n     <defs>\n      <path d="\nM39.4062 66.2188\nQ28.6562 66.2188 22.3281 58.2031\nQ16.0156 50.2031 16.0156 36.375\nQ16.0156 22.6094 22.3281 14.5938\nQ28.6562 6.59375 39.4062 6.59375\nQ50.1406 6.59375 56.4219 14.5938\nQ62.7031 22.6094 62.7031 36.375\nQ62.7031 50.2031 56.4219 58.2031\nQ50.1406 66.2188 39.4062 66.2188\nM39.4062 74.2188\nQ54.7344 74.2188 63.9062 63.9375\nQ73.0938 53.6562 73.0938 36.375\nQ73.0938 19.1406 63.9062 8.85938\nQ54.7344 -1.42188 39.4062 -1.42188\nQ24.0312 -1.42188 14.8125 8.82812\nQ5.60938 19.0938 5.60938 36.375\nQ5.60938 53.6562 14.8125 63.9375\nQ24.0312 74.2188 39.4062 74.2188" id="BitstreamVeraSans-Roman-4f"/>\n      <path d="\nM9.8125 72.9062\nL23.0938 72.9062\nL55.4219 11.9219\nL55.4219 72.9062\nL64.9844 72.9062\nL64.9844 0\nL51.7031 0\nL19.3906 60.9844\nL19.3906 0\nL9.8125 0\nz\n" id="BitstreamVeraSans-Roman-4e"/>\n      <path d="\nM8.01562 75.875\nL15.8281 75.875\nQ23.1406 64.3594 26.7812 53.3125\nQ30.4219 42.2812 30.4219 31.3906\nQ30.4219 20.4531 26.7812 9.375\nQ23.1406 -1.70312 15.8281 -13.1875\nL8.01562 -13.1875\nQ14.5 -2 17.7031 9.0625\nQ20.9062 20.125 20.9062 31.3906\nQ20.9062 42.6719 17.7031 53.6562\nQ14.5 64.6562 8.01562 75.875" id="BitstreamVeraSans-Roman-29"/>\n      <path d="\nM31 75.875\nQ24.4688 64.6562 21.2812 53.6562\nQ18.1094 42.6719 18.1094 31.3906\nQ18.1094 20.125 21.3125 9.0625\nQ24.5156 -2 31 -13.1875\nL23.1875 -13.1875\nQ15.875 -1.70312 12.2344 9.375\nQ8.59375 20.4531 8.59375 31.3906\nQ8.59375 42.2812 12.2031 53.3125\nQ15.8281 64.3594 23.1875 75.875\nz\n" id="BitstreamVeraSans-Roman-28"/>\n      <path d="\nM9.42188 54.6875\nL18.4062 54.6875\nL18.4062 0\nL9.42188 0\nz\n\nM9.42188 75.9844\nL18.4062 75.9844\nL18.4062 64.5938\nL9.42188 64.5938\nz\n" id="BitstreamVeraSans-Roman-69"/>\n      <path d="\nM-0.296875 72.9062\nL61.375 72.9062\nL61.375 64.5938\nL35.5 64.5938\nL35.5 0\nL25.5938 0\nL25.5938 64.5938\nL-0.296875 64.5938\nz\n" id="BitstreamVeraSans-Roman-54"/>\n      <path d="\nM8.6875 72.9062\nL18.6094 72.9062\nL18.6094 28.6094\nQ18.6094 16.8906 22.8438 11.7344\nQ27.0938 6.59375 36.625 6.59375\nQ46.0938 6.59375 50.3438 11.7344\nQ54.5938 16.8906 54.5938 28.6094\nL54.5938 72.9062\nL64.5 72.9062\nL64.5 27.3906\nQ64.5 13.1406 57.4375 5.85938\nQ50.3906 -1.42188 36.625 -1.42188\nQ22.7969 -1.42188 15.7344 5.85938\nQ8.6875 13.1406 8.6875 27.3906\nz\n" id="BitstreamVeraSans-Roman-55"/>\n      <path d="\nM64.4062 67.2812\nL64.4062 56.8906\nQ59.4219 61.5312 53.7812 63.8125\nQ48.1406 66.1094 41.7969 66.1094\nQ29.2969 66.1094 22.6562 58.4688\nQ16.0156 50.8281 16.0156 36.375\nQ16.0156 21.9688 22.6562 14.3281\nQ29.2969 6.6875 41.7969 6.6875\nQ48.1406 6.6875 53.7812 8.98438\nQ59.4219 11.2812 64.4062 15.9219\nL64.4062 5.60938\nQ59.2344 2.09375 53.4375 0.328125\nQ47.6562 -1.42188 41.2188 -1.42188\nQ24.6562 -1.42188 15.125 8.70312\nQ5.60938 18.8438 5.60938 36.375\nQ5.60938 53.9531 15.125 64.0781\nQ24.6562 74.2188 41.2188 74.2188\nQ47.75 74.2188 53.5312 72.4844\nQ59.3281 70.75 64.4062 67.2812" id="BitstreamVeraSans-Roman-43"/>\n     </defs>\n     <g transform="translate(23.52875 93.4903125)rotate(-90.0)scale(0.14 -0.14)">\n      <use xlink:href="#BitstreamVeraSans-Roman-43"/>\n      <use x="69.82421875" xlink:href="#BitstreamVeraSans-Roman-4f"/>\n      <use x="148.53515625" xlink:href="#BitstreamVeraSans-Roman-55"/>\n      <use x="221.728515625" xlink:href="#BitstreamVeraSans-Roman-4e"/>\n      <use x="296.533203125" xlink:href="#BitstreamVeraSans-Roman-54"/>\n      <use x="357.6171875" xlink:href="#BitstreamVeraSans-Roman-28"/>\n      <use x="396.630859375" xlink:href="#BitstreamVeraSans-Roman-69"/>\n      <use x="424.4140625" xlink:href="#BitstreamVeraSans-Roman-64"/>\n      <use x="487.890625" xlink:href="#BitstreamVeraSans-Roman-29"/>\n     </g>\n    </g>\n   </g>\n   <g id="patch_5">\n    <path d="\nM56.2303 19.26\nL131.04 19.26" style="fill:none;stroke:#000000;"/>\n   </g>\n   <g id="patch_6">\n    <path d="\nM131.04 95.94\nL131.04 19.26" style="fill:none;stroke:#000000;"/>\n   </g>\n   <g id="patch_7">\n    <path d="\nM56.2303 95.94\nL131.04 95.94" style="fill:none;stroke:#000000;"/>\n   </g>\n   <g id="patch_8">\n    <path d="\nM56.2303 95.94\nL56.2303 19.26" style="fill:none;stroke:#000000;"/>\n   </g>\n   <g id="text_11">\n    <!-- 70.0 -->\n    <defs>\n     <path d="\nM10.6875 12.4062\nL21 12.4062\nL21 0\nL10.6875 0\nz\n" id="BitstreamVeraSans-Roman-2e"/>\n     <path d="\nM8.20312 72.9062\nL55.0781 72.9062\nL55.0781 68.7031\nL28.6094 0\nL18.3125 0\nL43.2188 64.5938\nL8.20312 64.5938\nz\n" id="BitstreamVeraSans-Roman-37"/>\n    </defs>\n    <g transform="translate(69.7371875 66.04754375)scale(0.11 -0.11)">\n     <use xlink:href="#BitstreamVeraSans-Roman-37"/>\n     <use x="63.623046875" xlink:href="#BitstreamVeraSans-Roman-30"/>\n     <use x="127.24609375" xlink:href="#BitstreamVeraSans-Roman-2e"/>\n     <use x="159.033203125" xlink:href="#BitstreamVeraSans-Roman-30"/>\n    </g>\n   </g>\n   <g id="text_12">\n    <!-- 30.0 -->\n    <defs>\n     <path d="\nM40.5781 39.3125\nQ47.6562 37.7969 51.625 33\nQ55.6094 28.2188 55.6094 21.1875\nQ55.6094 10.4062 48.1875 4.48438\nQ40.7656 -1.42188 27.0938 -1.42188\nQ22.5156 -1.42188 17.6562 -0.515625\nQ12.7969 0.390625 7.625 2.20312\nL7.625 11.7188\nQ11.7188 9.32812 16.5938 8.10938\nQ21.4844 6.89062 26.8125 6.89062\nQ36.0781 6.89062 40.9375 10.5469\nQ45.7969 14.2031 45.7969 21.1875\nQ45.7969 27.6406 41.2812 31.2656\nQ36.7656 34.9062 28.7188 34.9062\nL20.2188 34.9062\nL20.2188 43.0156\nL29.1094 43.0156\nQ36.375 43.0156 40.2344 45.9219\nQ44.0938 48.8281 44.0938 54.2969\nQ44.0938 59.9062 40.1094 62.9062\nQ36.1406 65.9219 28.7188 65.9219\nQ24.6562 65.9219 20.0156 65.0312\nQ15.375 64.1562 9.8125 62.3125\nL9.8125 71.0938\nQ15.4375 72.6562 20.3438 73.4375\nQ25.25 74.2188 29.5938 74.2188\nQ40.8281 74.2188 47.3594 69.1094\nQ53.9062 64.0156 53.9062 55.3281\nQ53.9062 49.2656 50.4375 45.0938\nQ46.9688 40.9219 40.5781 39.3125" id="BitstreamVeraSans-Roman-33"/>\n    </defs>\n    <g transform="translate(94.641953125 81.38354375)scale(0.11 -0.11)">\n     <use xlink:href="#BitstreamVeraSans-Roman-33"/>\n     <use x="63.623046875" xlink:href="#BitstreamVeraSans-Roman-30"/>\n     <use x="127.24609375" xlink:href="#BitstreamVeraSans-Roman-2e"/>\n     <use x="159.033203125" xlink:href="#BitstreamVeraSans-Roman-30"/>\n    </g>\n   </g>\n  </g>\n </g>\n <defs>\n  <clipPath id="p7b8e183f0b">\n   <rect height="76.68" width="74.8096875" x="56.2303125" y="19.26"/>\n  </clipPath>\n </defs>\n</svg>\n';
        nodes.push({id: nodeid[i], image: "data:image/svg+xml;base64," + svglist[i], shape: 'image'});
    }
    for(var i=0; i<edgelist.length; i=i+2){
        edges.push({from: edgelist[i], to: edgelist[i+1], length: 200, arrows:'to'});
    }
    // create a network
    var container = document.getElementById('mynetwork');
    var data = {
        nodes: nodes,
        edges: edges
    };
    var options = {
        nodes: {
          borderWidth:2,
          size:30,
          color: {
              border: '#C0E0FD',
              background: '#FFFFFF'
            },
          font:{color:'#0B131A',
                size:8
          },
          shapeProperties: {
              useBorderWithImage:true
            }
        },
        physics: {stabilization: false},
        edges: {smooth: false,
                color: 'lightblue'
        }
    };
    network = new vis.Network(container, data, options);
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