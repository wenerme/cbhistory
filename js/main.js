// region 中文 moment js
(function (factory)
{
	factory(moment);
}(function (moment)
{
	return moment.lang('zh-cn', {
		months: "一月_二月_三月_四月_五月_六月_七月_八月_九月_十月_十一月_十二月".split("_"),
		monthsShort: "1月_2月_3月_4月_5月_6月_7月_8月_9月_10月_11月_12月".split("_"),
		weekdays: "星期日_星期一_星期二_星期三_星期四_星期五_星期六".split("_"),
		weekdaysShort: "周日_周一_周二_周三_周四_周五_周六".split("_"),
		weekdaysMin: "日_一_二_三_四_五_六".split("_"),
		longDateFormat: {
			LT: "Ah点mm",
			L: "YYYY-MM-DD",
			LL: "YYYY年MMMD日",
			LLL: "YYYY年MMMD日LT",
			LLLL: "YYYY年MMMD日ddddLT",
			l: "YYYY-MM-DD",
			ll: "YYYY年MMMD日",
			lll: "YYYY年MMMD日LT",
			llll: "YYYY年MMMD日ddddLT"
		},
		meridiem: function (hour, minute, isLower)
		{
			var hm = hour * 100 + minute;
			if (hm < 600)
			{
				return "凌晨";
			} else if (hm < 900)
			{
				return "早上";
			} else if (hm < 1130)
			{
				return "上午";
			} else if (hm < 1230)
			{
				return "中午";
			} else if (hm < 1800)
			{
				return "下午";
			} else
			{
				return "晚上";
			}
		},
		calendar: {
			sameDay: function ()
			{
				return this.minutes() === 0 ? "[今天]Ah[点整]" : "[今天]LT";
			},
			nextDay: function ()
			{
				return this.minutes() === 0 ? "[明天]Ah[点整]" : "[明天]LT";
			},
			lastDay: function ()
			{
				return this.minutes() === 0 ? "[昨天]Ah[点整]" : "[昨天]LT";
			},
			nextWeek: function ()
			{
				var startOfWeek, prefix;
				startOfWeek = moment().startOf('week');
				prefix = this.unix() - startOfWeek.unix() >= 7 * 24 * 3600 ? '[下]' : '[本]';
				return this.minutes() === 0 ? prefix + "dddAh点整" : prefix + "dddAh点mm";
			},
			lastWeek: function ()
			{
				var startOfWeek, prefix;
				startOfWeek = moment().startOf('week');
				prefix = this.unix() < startOfWeek.unix() ? '[上]' : '[本]';
				return this.minutes() === 0 ? prefix + "dddAh点整" : prefix + "dddAh点mm";
			},
			sameElse: 'LL'
		},
		ordinal: function (number, period)
		{
			switch (period)
			{
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
		relativeTime: {
			future: "%s内",
			past: "%s前",
			s: "几秒",
			m: "1分钟",
			mm: "%d分钟",
			h: "1小时",
			hh: "%d小时",
			d: "1天",
			dd: "%d天",
			M: "1个月",
			MM: "%d个月",
			y: "1年",
			yy: "%d年"
		},
		week: {
			// GB/T 7408-1994《数据元和交换格式·信息交换·日期和时间表示法》与ISO 8601:1988等效
			dow: 1, // Monday is the first day of the week.
			doy: 4  // The week that contains Jan 4th is the first week of the year.
		}
	});
}));
// endregion

/**
 * 隐藏和显示目标对象 data-toggle-target
 */
$(function ()
{
	$(document).on('click', '[data-toggle-target]', function ()
	{
		var $this = $(this);
		var target = $this.data('toggle-target');
		var $target = $(target);

		$target.slideToggle(function () {$this.text($target.is(':visible') ? "隐藏" : "显示")});
	});
});

/**
 * 主题切换
 */
$(function ()
{
	var reset = function ()
	{
		setTimeout(function ()
		{
			var y = $(".navbar").height() + 10;
			//$("#switcher-container").css('top', y+'px');
		}, 500);
	};
	reset();
	$('#switcher-bootstrap').on('click', 'a[data-theme]', function ()
	{
		var $this = $(this),
			theme = $this.attr('data-theme');
		$('#bootstrap-current').text($this.text());
		$('#bootstrap-css').attr('href', 'assets/bootstrap/css/bootstrap' + (theme === 'normal' ? '' : '-' + theme) + '.css');
		reset();
	})
		// 设置默认的主题
		//.find('[data-theme=simplex]').click()
	;
});

$(function()
{
	// page js 暂不支持 hashbang 所以暂时不做这些页面
	if(false)
	{
		page("/stat",function()
		{
			console.log("统计页面");
		});
		page("#/about",function()
		{
			console.log("关于页面");
		});

		page("/",function()
		{
			console.log("主页面");
		});
		page("#/hot",function()
		{
			console.log("热门评论页面");
		});
		page("*",function()
		{
			console.log("404", arguments);
		});
		page.base(location.pathname.substr(0,location.pathname.length-1));
		page();
	}
});