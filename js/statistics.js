(function (global)
{
	// 代表数据, 不用 data, 为了避免不必要的冲突
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
		doLoading(true);
		$.getJSON(url, function ()
		{
			doLoading(false);
			cb.apply(this, arguments);
		});
	};
})(window);

(function (global)
{
	var charts = {};
	var defaultChart = null;

	function getChart(code, category, options)
	{
		var cb = charts[getKey(code, category)] || defaultChart;
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

	var Charts = {};
	Charts.getChart = getChart;
	Charts.registerDefaultChart = registerDefaultChart;
	Charts.registerChart = registerChart;
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
