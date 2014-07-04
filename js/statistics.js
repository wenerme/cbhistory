// 代表数据, 不用 data, 为了避免不必要的冲突
var Datum = {};
Datum.getDataUrl = function (code, category)
{
	return "data/" + code + "-" + category + ".json";
};
Datum.loadData = function (code, category, cb)
{
	var url = Datum.getDataUrl(code, category);
	console.log("Load data ", url);
	$.getJSON(url, cb)
};

var ChartItem = Ractive.extend(
	{
		template: "#chart-item-tpl",
		init: function (options)
		{
			//this._super(options);
			this.observe("selectedCategory", function (newValue, oldValue, keypath)
			{
				this.updateChart();
			});

			this.updateChart();
		},
		updateChart: function ()
		{
			var self = this;

			Datum.loadData(this.data.code, this.data.selectedCategory, function (data)
			{
				nv.addGraph(function ()
				{
					var chart = nv.models.pieChart()
						.x(function (d) { return d.label })
						.y(function (d) { return d.value })
						.labelThreshold(.02)
						.labelType("percent")
						.showLabels(true);

					d3.select(self.find("svg"))
						.datum(data)
						.transition().duration(1200)
						.call(chart);

					nv.utils.windowResize(chart.update);

					window.chart2 = chart;

					return chart;
				});
			});
		}

	});


$(function ()
{
	loadChart("source-count");
	loadChart("area-count");

	function loadChart(code)
	{
		Datum.loadData(code, "info", function (data)
		{
			var ractive = new ChartItem({
				data: data
			});
			ractive.insert("#chart-item-container");
			window.ractive = ractive;
		});
	}
});
