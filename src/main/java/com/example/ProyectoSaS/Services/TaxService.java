package com.example.ProyectoSaS.Services;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class TaxService {

    private static final Map<String, BigDecimal> TAX_RATES = new HashMap<>();

    static {
        TAX_RATES.put("ES", new BigDecimal("21"));
        TAX_RATES.put("DE", new BigDecimal("19"));
        TAX_RATES.put("FR", new BigDecimal("20"));
        TAX_RATES.put("IT", new BigDecimal("22"));
        TAX_RATES.put("PT", new BigDecimal("23"));
        TAX_RATES.put("EN", new BigDecimal("20"));
        TAX_RATES.put("MX", new BigDecimal("16"));
        TAX_RATES.put("AR", new BigDecimal("21"));
        TAX_RATES.put("CO", new BigDecimal("19"));
        TAX_RATES.put("CL", new BigDecimal("19"));
        TAX_RATES.put("US", new BigDecimal("0"));
        TAX_RATES.put("CA", new BigDecimal("5"));
    }

    public BigDecimal obtenerTasaImpuesto(String pais) {
        if (pais == null || pais.isEmpty()) {
            pais = "ES";
        }
        return TAX_RATES.getOrDefault(pais.toUpperCase(), new BigDecimal("21"));
    }

    public BigDecimal calcularMontoImpuesto(BigDecimal montoBase, String pais) {
        BigDecimal tasa = obtenerTasaImpuesto(pais);
        return montoBase
            .multiply(tasa)
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotalConImpuesto(BigDecimal montoBase, String pais) {
        BigDecimal impuesto = calcularMontoImpuesto(montoBase, pais);
        return montoBase.add(impuesto);
    }

    public String obtenerNombrePais(String pais) {
        Map<String, String> nombres = new HashMap<>();
        nombres.put("ES", "España");
        nombres.put("DE", "Alemania");
        nombres.put("FR", "Francia");
        nombres.put("IT", "Italia");
        nombres.put("PT", "Portugal");
        nombres.put("EN", "Reino Unido");
        nombres.put("MX", "México");
        nombres.put("AR", "Argentina");
        nombres.put("CO", "Colombia");
        nombres.put("CL", "Chile");
        nombres.put("US", "Estados Unidos");
        nombres.put("CA", "Canadá");
        
        return nombres.getOrDefault(pais != null ? pais.toUpperCase() : "ES", pais);
    }
}
