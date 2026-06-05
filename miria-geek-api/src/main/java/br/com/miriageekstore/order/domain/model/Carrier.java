package br.com.miriageekstore.order.domain.model;

public enum Carrier {

    CORREIOS("https://rastreamento.correios.com.br/app/index.php?objeto=%s"),
    JADLOG("https://www.jadlog.com.br/jadlog/tracking.jad?cte=%s"),
    LOGGI("https://www.loggi.com/rastreador/?termo=%s"),
    TOTAL_EXPRESS("https://www.totalexpress.com.br/rastreio?codigo=%s"),
    AZUL_CARGO("https://www.azulcargo.com.br/rastreamento?awb=%s"),
    OUTRO(null);

    private final String urlTemplate;

    Carrier(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    public String buildTrackingUrl(String trackingCode, String customUrl) {
        if (this == OUTRO) return customUrl;
        return String.format(urlTemplate, trackingCode);
    }
}
