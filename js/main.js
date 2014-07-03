/**
 * Created by Wener on 2014/7/3.
 */
/*These lines are all chart setup.  Pick and choose which chart features you want to utilize. */
//		init();
function init()
{
	nv.addGraph(function ()
	{
		var chart = nv.models.lineChart()
				.margin({left: 100})  //Adjust chart margins to give the x-axis some breathing room.
				.useInteractiveGuideline(true)  //We want nice looking tooltips and a guideline!
				.transitionDuration(350)  //how fast do you want the lines to transition?
				.showLegend(false)       //Show the legend, allowing users to turn on/off line series.
				.showYAxis(true)        //Show the y-axis
				.showXAxis(true)        //Show the x-axis
				.x(function (d) {return d[1]})
				.y(function (d) {return d[0]})
			;

		chart.xAxis     //Chart x-axis settings
			.axisLabel('日期')
//					.tickFormat(d3.format(',r'))
			.tickFormat(function (d)
			{
				return d3.time.format('%Y-%m-%d')(new Date(d))
			})
		;

		chart.yAxis     //Chart y-axis settings
			.axisLabel('文章数量')
			.tickFormat(d3.format('d'));

		/* Done setting the chart up? Time to render it!*/
//			var myData = sinAndCos();   //You need data...
		var myData = formatSourceDateCount(DATA.SourceDateCount);

		d3.select('#chart svg')    //Select the <svg> element you want to render the chart in.
			.datum(myData)         //Populate the <svg> element with chart data...
			.call(chart);          //Finally, render the chart!

		//Update the chart when window resizes.
		nv.utils.windowResize(function () { chart.update() });
		return chart;
	});
}
function initPie(data)
{
	nv.addGraph(function ()
	{
		var chart = nv.models.pieChart()
			.showLegend(false)
			.x(function (d) { return d.label })
			.y(function (d) { return d.value })
			.showLabels(true);

		d3.select("#chart svg")
			.datum(data)
			.transition().duration(1200)
			.call(chart);

		return chart;
	});
}
$(function ()
{
	$.getJSON('data/total-top-source-count.json', function (d) {initPie(d);})
});
/**************************************
 * Simple test data generator
 */
function sinAndCos()
{
	var sin = [], sin2 = [],
		cos = [];

	//Data is represented as an array of {x,y} pairs.
	for (var i = 0; i < 100; i++)
	{
		sin.push({x: i, y: Math.sin(i / 10)});
		sin2.push({x: i, y: Math.sin(i / 10) * 0.25 + 0.5});
		cos.push({x: i, y: .5 * Math.cos(i / 10)});
	}

	//Line chart data should be sent as an array of series objects.
	return [
		{
			values: sin,      //values - represents the array of {x,y} data points
			key: 'Sine Wave', //key  - the name of the series.
			color: '#ff7f0e'  //color - optional: choose your own line color.
		},
		{
			values: cos,
			key: 'Cosine Wave',
			color: '#2ca02c'
		},
		{
			values: sin2,
			key: 'Another sine wave',
			color: '#7777ff',
			area: true      //area - set to true if you want this line to turn into a filled area chart.
		}
	];
}

function formatSourceDateCount(source)
{
	var dest = [];
	var maxTime = Number.MIN_VALUE;
	var minTime = Number.MAX_VALUE;
	// 查找最大最小时间, 转换时间戳
	for (key in source)
	{
		if (!source.hasOwnProperty(key))
			continue;
		if (key == "null")
			continue;

		var item = {};
		dest.push(item);
		item.key = key;
		var values = {};
		item.values = values;
		var val = source[key];
		for (time in val)
		{
			if (!val.hasOwnProperty(time))
				continue;
			var timestamp = moment(time, "yyyy-MM-dd").valueOf();
			if (timestamp > maxTime)
				maxTime = timestamp;
			if (timestamp < minTime)
				minTime = timestamp;

//					values.push([val[time], timestamp])
			values[timestamp] = val[time];// 转换为时间戳
		}
	}
	// 生成统一的对象
	var m = moment(minTime);
	var common = {};
	while (1)
	{
		if (m.valueOf() > maxTime)
			break;
		common[m.valueOf()] = 0;
		m = m.add("days", 1);
	}
	// 转换为数组
	dest.forEach(function (v, i)
	{
		var val = $.extend({}, common, v.values);
		v.values = $.map(val, function (v, k)
		{
			return [
				[v, k]
			];
		});
	});

	return dest;
}