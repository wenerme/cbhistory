/**
 * Datum 数据操作工具类
 */
(function (global)
{
	// datum 代表数据, 不用 data, 为了避免不必要的冲突
	var loading = false;
	var $loading = $("#loading-alert").hide();
	var Datum = {};
	global.Datum = Datum;

	Datum.getDataUrl = function (code, category)
	{
		return "data/" + code + "-" + category + ".json";
	};
	function doLoading(isLoading)
	{
		loading = isLoading;
		window.setTimeout(function()
		{
			if(loading)
				$loading.slideDown();
			else
				$loading.slideUp();
		}, 200);
	}
	Datum.loadData = function (code, category, cb)
	{
		var url = Datum.getDataUrl(code, category);
		console.log("Load data ", url);
		cb && doLoading(true);
		// 有可能使用 promise 的方式调用
		return cb?
		$.getJSON(url, function ()
		{
			doLoading(false);
			cb.apply(this, arguments);
		}):
		$.getJSON(url);
	};
})(window);

/**
 * Charts 用于获取chart
 */
(function (global)
{
	var charts = {};
	var doneHandler = {};
	var defaultChart = null;

	function getChart(code, category, options)
	{
		var cb = charts[getKey(code, category)] || charts[getKey(code,"*")] || charts[getKey("*","*")] || defaultChart;
		if (!cb)
		{
			console.error("无法处理表 " + code + " - " + category);
			return null;
		}
		return cb(options);
	}

	function getKey(code, category)
	{return code + "$" + category;}

	function registerChart(code, category, cb)
	{
		charts[getKey(code, category)] = cb;
	}

	function registerDefaultChart(cb)
	{
		defaultChart = cb;
	}
	function done(code, category, cb)
	{
		if(! cb)
		{
			cb = doneHandler[getKey(code, category)] || doneHandler[getKey(code,"*")] || doneHandler[getKey("*","*")];
			return cb && cb(code, category);
		}else{
			doneHandler[getKey(code, category)] = cb;
		}
	}

	var Charts = {};
	Charts.getChart = getChart;
	Charts.registerDefaultChart = registerDefaultChart;
	Charts.registerChart = registerChart;
	Charts.done = done;
	global.Charts = Charts;
})(window);

var ChartItem = Ractive.extend(
	{
		template: "#chart-item-tpl",
		init: function (options)
		{
			//this._super(options);


			this.observe("showCategory", function (newValue, oldValue, keypath)
			{
				this.updateChart();
			});

			this.updateChart();
		},
		updateChart: function ()
		{
			var self = this;

			Datum.loadData(this.data.code, this.data.showCategory, function (data)
			{
				nv.addGraph(function ()
				{
					var chart = Charts.getChart(self.data.code, self.data.showCategory);

					d3.select(self.find("svg"))
						.datum(data)
						.transition()
						.duration(1200)
						.call(chart);

					nv.utils.windowResize(chart.update);

					window.chart2 = chart;

					return chart;
				});
				// 完成一个数据表的加载
				Charts.done(self.data.code, self.data.showCategory);
			});
		},
		data: { }
	});


$(function ()
{

	Charts.registerDefaultChart(function ()
	{
		var chart = nv.models.pieChart()
			.x(function (d) { return d.label })
			.y(function (d) { return d.value })
			.labelThreshold(.02)
			.labelType("percent")
			.showLabels(true);
		return chart;
	});

	var loadLine = {"publisher-active-time":true,"commenter-active-time":true};
	Charts.done("*","*", function(code)
	{
		if(!loadLine[code])
			return;
		loadLineChart(code);
		loadLine[code] = false;
	});

	// 初始化
	//if(false)
	Datum.loadData("data", "info", function (data)
	{
		data.formatValue = function (val)
		{
			console.log("format ", val);
			// 尝试格式化日期
			if (!/^\d+$/.test(val))
			{
				var m = moment(val);
				if (!m.isValid())
					return val;
				return m.format('llll')+" ("+ m.fromNow()+")";
			}
			return val;
		};

		var ractive = new Ractive({
			el: "#system-info",
			template: "#system-info-tpl",
			data: data
		});

		$.each(data.codes, function ()
		{
			loadChart(this);
		});
	});

	function loadChart(code)
	{
		Datum.loadData(code, "info", function (data)
		{
			var ractive = new ChartItem({data: data});
			ractive.insert("#chart-item-container");
			window.ractive = ractive;
		});
	}


});
// 将一系列的饼图数据转换为折线图
function loadLineChart(code)
{
	var $section = $('#'+code);
	var $options = $section.find('option[value*=total]');
	var names = [];

	return $.when.apply($, $options.map(function(v,k)
	{
		var $this = $(this);
		names.push($this.val());
		names[$this.val()] = $this.text();

		return Datum.loadData(code, $this.val());
	})).then(function()
	{
		window.allData = arguments;
		var args = arguments;
		var data = [];
		$.each(names, function(i,v)
		{
			data.push({key:names[v], values: args[i][0]})
		});
		return data;
	}).then(function(data)
	{
		//if(false)
		nv.addGraph(function() {
			var chart = nv.models.lineChart()
					.margin({left: 100})  //Adjust chart margins to give the x-axis some breathing room.
					.useInteractiveGuideline(true)  //We want nice looking tooltips and a guideline!
					.transitionDuration(350)  //how fast do you want the lines to transition?
					.showLegend(true)       //Show the legend, allowing users to turn on/off line series.
					.showYAxis(true)        //Show the y-axis
					.showXAxis(true)        //Show the x-axis
					.x(function(d){return +d.label})
					.y(function(d){return d.value})
				;

			chart.xAxis
				.axisLabel('时')
				.tickFormat(d3.format('d'))
			;

			chart.yAxis
				.axisLabel('数量')
				.tickFormat(d3.format('d'))
			;

//			d3.select('#chart svg')
			d3.select($('<div class="chart"><svg/></div>').appendTo($section).find('svg')[0])
				.datum(data)
				.transition().duration(500)
				.call(chart)
			;

			nv.utils.windowResize(chart.update);

			return chart;
		});

		return data;
	});
}