var map = d3.select('#map');
var tooltip = map.append("div").attr("class", "tooltip hidden");

var width = parseInt(map.style('width')),
	height = parseInt(map.style('height'));

var projection = d3.geo.equirectangular()
	.center([0, 0]).scale(width/2/Math.PI)
	.translate([width/2, height/2]);

var path = d3.geo.path().projection(projection);

var zoom = d3.behavior.zoom().translate([0, 0]).scale(1).scaleExtent([1, 40]).on("zoom", function(){
	var t = d3.event.translate;
	var s = d3.event.scale;

	var w_max = 0;
	var w_min = width * (1 - s);
	var h_max = height < s*width/2 ? s*(width/2-height)/2 : (1-s)*height/2;
	var h_min = height < s*width/2 ? -s*(width/2-height)/2-(s-1)*height : (1-s)*height/2;

	t[0] = Math.min(w_max, Math.max(w_min, t[0]));
	t[1] = Math.min(h_max, Math.max(h_min, t[1]));

	zoom.translate(t);
	g.attr("transform", "translate(" + t + ")scale(" + s + ")");
	g.selectAll("path").style("stroke-width", .5 / s + "px");
});

var svg = map
	.append("svg")
	.attr("width", '100%')
	.attr("height", '100%')
	.call(zoom);

var g = svg.append("g");

d3.json('world-topo-min.json', function(json) {
	g.selectAll("path")
		.data(topojson.feature(json, json.objects.countries).features)
		.enter().append("path")
		.attr("d", path)
		.on("mousemove", function(d,i){
			var mouse = d3.mouse(svg.node()).map( function(d) { return parseInt(d); } );
			var left = Math.min(width-12*d.properties.name.length, (mouse[0]+20));
			var top = Math.min(height-40, (mouse[1]+20));

			tooltip.classed("hidden", false)
				.attr("style", "left:"+left+"px;top:"+top+"px")
				.html(d.properties.name);
		})
		.on("mouseout",  function(d,i) {
			tooltip.classed("hidden", true);
		});

	d3.select(window).on('resize', function() {
		width = parseInt(map.style('width'));
		height = parseInt(map.style('height'));

		projection
			.scale(width/2/Math.PI)
			.translate([width/2, height/2]);

		g.selectAll("path")
			.attr("d", path);
	});
});