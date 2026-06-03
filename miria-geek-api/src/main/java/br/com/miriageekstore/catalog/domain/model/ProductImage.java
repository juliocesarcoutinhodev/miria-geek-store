package br.com.miriageekstore.catalog.domain.model;

import java.time.Instant;

public class ProductImage {

    private final ImageId id;
    private final ProductId productId;
    private final String url;
    private final String filename;
    private boolean principal;
    private int imageOrder;
    private final Instant createdAt;

    private ProductImage(ImageId id, ProductId productId, String url, String filename,
                         boolean principal, int imageOrder, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.url = url;
        this.filename = filename;
        this.principal = principal;
        this.imageOrder = imageOrder;
        this.createdAt = createdAt;
    }

    public static ProductImage create(ProductId productId, String url, String filename,
                                       boolean principal, int imageOrder) {
        return new ProductImage(
                ImageId.generate(), productId, url, filename,
                principal, imageOrder, Instant.now()
        );
    }

    public static ProductImage reconstitute(ImageId id, ProductId productId, String url,
                                             String filename, boolean principal,
                                             int imageOrder, Instant createdAt) {
        return new ProductImage(id, productId, url, filename, principal, imageOrder, createdAt);
    }

    public void setPrincipal(boolean principal) { this.principal = principal; }
    public void setImageOrder(int imageOrder) { this.imageOrder = imageOrder; }

    public ImageId getId() { return id; }
    public ProductId getProductId() { return productId; }
    public String getUrl() { return url; }
    public String getFilename() { return filename; }
    public boolean isPrincipal() { return principal; }
    public int getImageOrder() { return imageOrder; }
    public Instant getCreatedAt() { return createdAt; }
}
