
package com.example.currencyconverter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.swing.JOptionPane;
import org.json.JSONObject;

public class MAIN {
    private static final String API_KEY = "a4015bbb23989594a371e2e3"; // clave de API
    private static final String BASE_CURRENCY = "MXN"; // Peso mexicano

    public static void main(String[] args) {
        while (true) {
            String[] options = {"Convertir MXN a otra moneda", "Convertir otra moneda a MXN"};
            int choice = JOptionPane.showOptionDialog(null, "Seleccione una opción", "Conversor de Monedas",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == JOptionPane.CLOSED_OPTION) {
                break;
            }

            if (choice == 0) {
                convertFromMXN();
            } else if (choice == 1) {
                convertToMXN();
            }

            int continueChoice = JOptionPane.showConfirmDialog(null, "¿Desea realizar otra conversión?",
                    "Continuar", JOptionPane.YES_NO_CANCEL_OPTION);

            if (continueChoice != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Programa Terminado");
                break;
            }
        }
    }

    private static void convertFromMXN() {
        String[] currencies = {"USD", "EUR", "GBP", "JPY", "KRW"};
        String targetCurrency = (String) JOptionPane.showInputDialog(null, "Seleccione la moneda objetivo",
                "Conversor de Monedas", JOptionPane.QUESTION_MESSAGE, null, currencies, currencies[0]);

        if (targetCurrency != null) {
            String amountStr = JOptionPane.showInputDialog(null, "Ingrese la cantidad en MXN");
            if (isValidNumber(amountStr)) {
                double amount = Double.parseDouble(amountStr);
                try {
                    double rate = getExchangeRate(BASE_CURRENCY, targetCurrency);
                    double convertedAmount = amount * rate;
                    JOptionPane.showMessageDialog(null, amount + " MXN son equivalentes a " +
                            convertedAmount + " " + targetCurrency);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error al obtener la tasa de cambio: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido.");
            }
        }
    }

    private static void convertToMXN() {
        String[] currencies = {"USD", "EUR", "GBP", "JPY", "KRW"};
        String baseCurrency = (String) JOptionPane.showInputDialog(null, "Seleccione la moneda de origen",
                "Conversor de Monedas", JOptionPane.QUESTION_MESSAGE, null, currencies, currencies[0]);

        if (baseCurrency != null) {
            String amountStr = JOptionPane.showInputDialog(null, "Ingrese la cantidad en " + baseCurrency);
            if (isValidNumber(amountStr)) {
                double amount = Double.parseDouble(amountStr);
                try {
                    double rate = getExchangeRate(baseCurrency, BASE_CURRENCY);
                    double convertedAmount = amount * rate;
                    JOptionPane.showMessageDialog(null, amount + " " + baseCurrency + " son equivalentes a " +
                            convertedAmount + " MXN");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error al obtener la tasa de cambio: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido.");
            }
        }
    }

    private static boolean isValidNumber(String str) {
        if (str == null) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static double getExchangeRate(String baseCurrency, String targetCurrency) throws Exception {
        String url = String.format("https://v6.exchangerate-api.com/v6/%s/latest/%s", API_KEY, baseCurrency);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());

        if (json.getString("result").equals("success")) {
            return json.getJSONObject("conversion_rates").getDouble(targetCurrency);
        } else {
            throw new Exception("Error en la respuesta de la API");
        }
    }
}
