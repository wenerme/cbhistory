/**
 * Created by Wener on 2014/7/3.
 */


(function (factory) {
	factory(moment);
}(function (moment) {
	return moment.lang('zh-cn', {
		months : "一月_二月_三月_四月_五月_六月_七月_八月_九月_十月_十一月_十二月".split("_"),
		monthsShort : "1月_2月_3月_4月_5月_6月_7月_8月_9月_10月_11月_12月".split("_"),
		weekdays : "星期日_星期一_星期二_星期三_星期四_星期五_星期六".split("_"),
		weekdaysShort : "周日_周一_周二_周三_周四_周五_周六".split("_"),
		weekdaysMin : "日_一_二_三_四_五_六".split("_"),
		longDateFormat : {
			LT : "Ah点mm",
			L : "YYYY-MM-DD",
			LL : "YYYY年MMMD日",
			LLL : "YYYY年MMMD日LT",
			LLLL : "YYYY年MMMD日ddddLT",
			l : "YYYY-MM-DD",
			ll : "YYYY年MMMD日",
			lll : "YYYY年MMMD日LT",
			llll : "YYYY年MMMD日ddddLT"
		},
		meridiem : function (hour, minute, isLower) {
			var hm = hour * 100 + minute;
			if (hm < 600) {
				return "凌晨";
			} else if (hm < 900) {
				return "早上";
			} else if (hm < 1130) {
				return "上午";
			} else if (hm < 1230) {
				return "中午";
			} else if (hm < 1800) {
				return "下午";
			} else {
				return "晚上";
			}
		},
		calendar : {
			sameDay : function () {
				return this.minutes() === 0 ? "[今天]Ah[点整]" : "[今天]LT";
			},
			nextDay : function () {
				return this.minutes() === 0 ? "[明天]Ah[点整]" : "[明天]LT";
			},
			lastDay : function () {
				return this.minutes() === 0 ? "[昨天]Ah[点整]" : "[昨天]LT";
			},
			nextWeek : function () {
				var startOfWeek, prefix;
				startOfWeek = moment().startOf('week');
				prefix = this.unix() - startOfWeek.unix() >= 7 * 24 * 3600 ? '[下]' : '[本]';
				return this.minutes() === 0 ? prefix + "dddAh点整" : prefix + "dddAh点mm";
			},
			lastWeek : function () {
				var startOfWeek, prefix;
				startOfWeek = moment().startOf('week');
				prefix = this.unix() < startOfWeek.unix()  ? '[上]' : '[本]';
				return this.minutes() === 0 ? prefix + "dddAh点整" : prefix + "dddAh点mm";
			},
			sameElse : 'LL'
		},
		ordinal : function (number, period) {
			switch (period) {
				case "d":
				case "D":
				case "DDD":
					return number + "日";
				case "M":
					return number + "月";
				case "w":
				case "W":
					return number + "周";
				default:
					return number;
			}
		},
		relativeTime : {
			future : "%s内",
			past : "%s前",
			s : "几秒",
			m : "1分钟",
			mm : "%d分钟",
			h : "1小时",
			hh : "%d小时",
			d : "1天",
			dd : "%d天",
			M : "1个月",
			MM : "%d个月",
			y : "1年",
			yy : "%d年"
		},
		week : {
			// GB/T 7408-1994《数据元和交换格式·信息交换·日期和时间表示法》与ISO 8601:1988等效
			dow : 1, // Monday is the first day of the week.
			doy : 4  // The week that contains Jan 4th is the first week of the year.
		}
	});
}));


$(function()
{
	$(document).on('click','[data-toggle-target]', function()
	{
		var $this = $(this);
		var target = $this.data('toggle-target');
		var $target = $(target);

		$target.slideToggle(function(){$this.text($target.is(':visible')?"隐藏":"显示")});
	});
});


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
//			.showLegend(false)
			.width(900)
			.height(500)
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
	$('.chart-item').each(function()
	{
		loadChart(this);
	});

	$(document).on('change', '.chart-item select', function()
	{
		var $this = $(this);
		loadChart($this.closest('.chart-item'));
	});

	function initChart(code)
	{

	}

	function loadChart(chartItem)
	{
		var $this = $(chartItem);
		var url = $this.data('data-url');
		if(!url)
			return;

		url = url.replace("?", $this.find(".date-range :selected").val());
		console.log("Load chart data ", url);
		$.getJSON(url, function (data)
		{
			nv.addGraph(function ()
			{
				var chart = nv.models.pieChart()
					.x(function (d) { return d.label })
					.y(function (d) { return d.value })
					.labelThreshold(.02)
					.labelType("percent")
					.showLabels(true);

				d3.select($this.find("svg")[0])
					.datum(data)
					.transition().duration(1200)
					.call(chart);

				nv.utils.windowResize(chart.update);

				window.chart = chart;

				return chart;
			});
		})
	}

	if(false)
	$('[data-chart-data]').each(function()
	{
		var $this = $(this);
		var url = $this.data('chart-data');
		console.log("Load chart data ", url);
		var setting = {};
		setting.width = $this.data("chart-width");
		setting.height = $this.data("chart-height");
		$.getJSON(url, function (data)
		{
			$this.css(setting);
			nv.addGraph(function ()
			{
				var chart = nv.models.pieChart()
					.width(setting.width)
					.height(setting.height)
					.x(function (d) { return d.label })
					.y(function (d) { return d.value })
					.labelThreshold(.02)
					.labelType("percent")
					.showLabels(true);

				d3.select($this.find("svg")[0])
					.datum(data)
					.transition().duration(1200)
					.call(chart);

				window.chart = chart;

				return chart;
			});
		})
	});
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