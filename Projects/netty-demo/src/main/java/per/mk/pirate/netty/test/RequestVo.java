package per.mk.pirate.netty.test;

import java.math.BigDecimal;

public class RequestVo implements java.io.Serializable {

    public RequestVo() {
    }

    public RequestVo(String serviceId) {
        this(serviceId, null);
    }

    public RequestVo(String serviceId, Integer auctionId) {
        this(serviceId, auctionId, null);
    }

    public RequestVo(String serviceId, Integer auctionId, String memberId) {
        this(serviceId, auctionId, memberId, null);
    }

    public RequestVo(String serviceId, Integer auctionId, String memberId,
                     BigDecimal price) {
        this(serviceId, auctionId, memberId, price, null);
    }

    public RequestVo(String serviceId, Integer auctionId, String memberId,
                     BigDecimal price, Integer avId) {
        this.serviceId = serviceId;
        this.auctionId = auctionId;
        this.memberId = memberId;
        this.price = price;
        this.avId = avId;
    }

    public RequestVo(String serviceId, Integer auctionId, String memberId,
                     BigDecimal price, Integer avId, Integer packageId) {
        this.serviceId = serviceId;
        this.auctionId = auctionId;
        this.memberId = memberId;
        this.price = price;
        this.avId = avId;
        this.packageId = packageId;
    }

    private static final long serialVersionUID = 1L;

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * 会员ID
     */
    private String memberId;

    /**
     * 拍卖会ID
     */
    private Integer auctionId;

    /**
     * 拍品ID
     */
    private Integer avId;

    /**
     * 拍品包ID
     */
    private Integer packageId;

    /**
     * 最新出价
     */
    private BigDecimal price;

    /**
     * json数据
     */
    private String jsonData;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Integer getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Integer auctionId) {
        this.auctionId = auctionId;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public Integer getAvId() {
        return avId;
    }

    public void setAvId(Integer avId) {
        this.avId = avId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }
}
