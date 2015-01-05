package br.com.gdgaracaju.sunshine.app.util;

import java.util.Date;

public class WeatherDetail {
    public Date date;
    public TemperatureUnit temperatureUnit;
    public double maxTemperature;
    public double minTemperature;
    public String weatherCondition;
    public byte humidity;
    public int pressure;
    public String wind;

    public void changeTemperatureUnit(TemperatureUnit newTemperatureUnit){
        if (!newTemperatureUnit.equals(temperatureUnit)){
            if (newTemperatureUnit.equals(TemperatureUnit.CELSIUS)){
                maxTemperature = WeatherDataParser.convertFarenheitToCelsius(maxTemperature);
                minTemperature = WeatherDataParser.convertFarenheitToCelsius(minTemperature);
            } else {
                maxTemperature = WeatherDataParser.convertCelsiusToFarenheit(maxTemperature);
                minTemperature = WeatherDataParser.convertCelsiusToFarenheit(minTemperature);
            }
            temperatureUnit = newTemperatureUnit;
        }
    }
}