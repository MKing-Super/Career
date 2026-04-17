package per.mk.springai.demo.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@Slf4j
public class WeatherTools {

    private static final Map<String, String[]> CITY_WEATHER = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        CITY_WEATHER.put("北京", new String[]{"晴天", "多云", "小雨", "晴天", "多云"});
        CITY_WEATHER.put("上海", new String[]{"多云", "小雨", "阴天", "多云", "晴天"});
        CITY_WEATHER.put("广州", new String[]{"晴天", "雷阵雨", "多云", "晴天", "阴天"});
        CITY_WEATHER.put("深圳", new String[]{"多云", "晴天", "雷阵雨", "多云", "晴天"});
        CITY_WEATHER.put("杭州", new String[]{"多云", "晴天", "小雨", "阴天", "多云"});
        CITY_WEATHER.put("成都", new String[]{"阴天", "小雨", "多云", "晴天", "阴天"});
        CITY_WEATHER.put("武汉", new String[]{"晴天", "多云", "小雨", "阴天", "多云"});
        CITY_WEATHER.put("西安", new String[]{"晴天", "多云", "沙尘", "阴天", "晴天"});
        CITY_WEATHER.put("南京", new String[]{"多云", "晴天", "小雨", "阴天", "多云"});
        CITY_WEATHER.put("重庆", new String[]{"阴天", "小雨", "多云", "雾", "阴天"});
    }

    /**
     * 查询天气
     *
     * @param city 城市名称
     * @return 天气信息
     */
    @Tool(name = "查询天气", description = "根据城市名称查询天气信息，返回天气状况、温度、湿度、风力风向、发布时间等详细信息")
    public String queryWeather(@ToolParam(description = "城市名称，如：北京、上海、广州、深圳等") String city) {
        log.info("查询天气: {}", city);

        if (city == null || city.trim().isEmpty()) {
            return "请提供城市名称，例如：北京、上海、广州、深圳";
        }

        String weather = "未知";
        int temperature = 0;

        String[] weathers = CITY_WEATHER.get(city);
        if (weathers != null) {
            weather = weathers[RANDOM.nextInt(weathers.length)];
            temperature = 15 + RANDOM.nextInt(20);
        } else {
            weather = "多云";
            temperature = 18 + RANDOM.nextInt(12);
        }

        int humidity = 40 + RANDOM.nextInt(40);
        String[] windDirections = {"北风", "南风", "东风", "西风", "东南风", "东北风", "西南风", "西北风"};
        String[] windLevels = {"1级", "2级", "3级", "4级", "5级"};
        String wind = windDirections[RANDOM.nextInt(windDirections.length)] + windLevels[RANDOM.nextInt(windLevels.length)];

        String updateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return String.format(
                "%s 今日天气\n\n" +
                "天气状况：%s\n" +
                "温度：%d°C\n" +
                "湿度：%d%%\n" +
                "风力风向：%s\n" +
                "发布时间：%s\n\n" +
                "温馨提示：%s",
                city,
                weather,
                temperature,
                humidity,
                wind,
                updateTime,
                getWeatherTip(weather)
        );
    }

    /**
     * 获取穿衣建议
     *
     * @param city 城市名称
     * @return 穿衣建议
     */
    @Tool(name = "穿衣指数", description = "根据城市名称获取穿衣建议")
    public String getDressingAdvice(@ToolParam(description = "城市名称") String city) {
        log.info("获取穿衣建议: {}", city);

        String[] weathers = CITY_WEATHER.get(city);
        String weather = weathers != null ? weathers[RANDOM.nextInt(weathers.length)] : "多云";
        int temp = 15 + RANDOM.nextInt(20);

        String advice;
        if (temp >= 28) {
            advice = "天气炎热，建议穿短袖、短裤、裙子等轻薄衣物，注意防暑防晒";
        } else if (temp >= 22) {
            advice = "气温舒适，建议穿长袖、薄外套、衬衫等";
        } else if (temp >= 15) {
            advice = "早晚较凉，建议穿薄外套、针织衫、长裤等";
        } else {
            advice = "天气较冷，建议穿外套、毛衣、牛仔裤等保暖衣物";
        }

        return String.format("%s 穿衣建议：%s", city, advice);
    }

    /**
     * 获取出行建议
     *
     * @param city 城市名称
     * @return 出行建议
     */
    @Tool(name = "出行建议", description = "根据城市名称获取出行建议")
    public String getTravelAdvice(@ToolParam(description = "城市名称") String city) {
        log.info("获取出行建议: {}", city);

        String[] weathers = CITY_WEATHER.get(city);
        String weather = weathers != null ? weathers[RANDOM.nextInt(weathers.length)] : "多云";

        String advice;
        switch (weather) {
            case "晴天":
                advice = "天气晴朗，适合户外活动和出行，记得带墨镜和防晒霜";
                break;
            case "多云":
                advice = "多云天气，气温适宜，适合各类户外活动";
                break;
            case "小雨":
                advice = "有小雨，建议带雨伞或雨衣，注意路面湿滑";
                break;
            case "雷阵雨":
                advice = "雷阵雨天气，请尽量避免外出，室内活动更安全";
                break;
            case "阴天":
                advice = "阴天多云，建议携带外套，注意天气变化";
                break;
            case "沙尘":
                advice = "沙尘天气，建议戴口罩，尽量减少户外活动";
                break;
            case "雾":
                advice = "有雾，能见度较低，开车出行请注意安全，减速慢行";
                break;
            default:
                advice = "天气情况一般，出行请注意安全";
        }

        return String.format("%s 出行建议：%s", city, advice);
    }

    private String getWeatherTip(String weather) {
        switch (weather) {
            case "晴天":
                return "天气晴朗，阳光明媚，适合户外运动";
            case "多云":
                return "多云天气，气温适宜，适宜出行";
            case "小雨":
                return "有小雨，外出请携带雨具，注意保暖";
            case "雷阵雨":
                return "雷雨天气，请注意防雷，避免在空旷处逗留";
            case "阴天":
                return "天气阴沉，建议适当添加衣物";
            case "沙尘":
                return "沙尘天气，外出请佩戴口罩，做好防护";
            case "雾":
                return "有雾能见度低，出行请注意安全";
            default:
                return "天气多变，请关注实时预报";
        }
    }
}
