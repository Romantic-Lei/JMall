package config;

/**
 * 微信支付参数
 */
public class PayConfig {
	// 应用的APPID
	public static String APP_ID = "2016102200736518";
	// 生成的应用私钥
	public static String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCC2Q+msH90goQIDFodjg5QzEikuUVaWJfBw01EVTeKhE5XU0C2JRylEzgei4jftgXVtVTEWTkPka9rujeS5urrtNw12P2dAqP+9ufEPdR7NpX"
			+ "g9E9WSdrr2XGWRify7VrV5GcSuyPd9CbcWh+AoYF+nrArBL0+OpgeXjYSt6NLYl6wXdEIXHOuXnIwEFGgGd/Pi74BpfuCTX8etD5wiMpNIIi+3KuVy1ACrafgeXiVnk+NM4ktWEzYyfuGOsZZy41ONsTH1IC5AIzc0lS2SsgwJxErO8Tf92KaDyJ3u5BO"
			+ "/5M3Nlj/uy0ljqXp5kEg51hMXtvIk860iW7rYPAWwcYFAgMBAAECggEAfaYc7m+sZOB0ExpxatIgExyoYW38GC7cw9dcmlLOu/gjFp4+lOp8YFRdXo+Vh2XOopK3bLlVHwtCDcvLVAjk7CVIFXliyORhQRQuKXdKVGttb5CLowvW6wZqmeiSsieVQ7JJ/"
			+ "GWyoo+yxYTRnX75z/Jor1akt14i9352pHCtoVPT/6QiUNMcE6vesv2uVbW3Cfs7p0vfdhYqu+E6H0MHsTUztSl+Z1m9o4GzlLW3c7DYG3ziZxXgAcm3aaf+1yRVmL2AbwYTY0wujyaV7hzoaUZrCn9Igg4kWfdnwCnICsvywgdZ+cEB3caOjlqFpdL4xPU"
			+ "6IwsvzXs5JLitcTusQQKBgQDkVi+yyeGhELUj0x0YyHk3yNYCUtAYoV1jzG19IaZt55mWAXb5/fGT5vnNxM1HqNhDYdkJUvDGsL6bGB1R5fv3Q4KzQ831xhef8rHaqOcCPKvgnVb1Mn7TVZT0HRpOGNDi2+h7QHEAn6FLpi5Z6DYjGIynTCPbbhEihn/T88"
			+ "4sEQKBgQCSs0f2Dum9hWOOyzgNYVCbVfp13WXNdSu0frFr4tN6GleTIeZmuLa+VAc6zXKAKxyad+n4yRH+ZPUC0OA+kctHgySYhC6gGI8sdvhsuSmDBs/qviMFNnn6R3XAmyW9DrI0CT1TkSxG6XcGzB22ahO7ZmiUZWAinziyDqRvBMi+tQKBgAD2K/VAzG"
			+ "Z1P44LEZBNJ9WITv5rgtzXsSD+d2eTgLPtycPL9chPvXi1/E9ViNfIakC+ZL+ZTdJIZEUa8OSrZ0g/TImcu31RfD/8MauOEor73m1vvDIEKGyTa7ErfjTd/eM2sd/zAYbTJUbASGmwhKnZLJ53v57o88GFd3xZBoFBAoGBAIxX2Wsrvy+hcapE/h3G0J+atF6X7PGOu74USlYFDP3OP6OxlcMFyUXyC2yU6Yp2NAoO2BY6/eza1o7YFv+k6M1ER/lk01ozxJzsQCN7TJxeIwDW35f4Et9+B8e4ZXUlnxQrFrFgu/QnG8iz82T0vGXWxgvZpAvbORkW6vaOpCHVAoGAX1fxzI7x/+Hfw5slKJtGqNzBbrZi8/NwZYVhz9Xac5S1GmIf2RTERF0fhKCcxhcUB0AgcxZtKyuNOLILPRsLiYC3H8808c7VaQj1y06Xc+0d5tiJn6UmwhSjXwTc0COXgmFa3RQP/Xs102AiHkzwuRXkScZfVcxTE4JzOSmAQ5U=";
	public static String CHARSET = "UTF-8";
	// 支付宝公钥
	public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqv0Bo8tieSc5F+plqUvZunA/IgxVpm4AGZOtixB6C2bRicNpcOA5l9pjrI4Sa0ZVIfMwh5RDkT/GdfyZhUFjukxV5AS9oU3Lp/rxLm25CLtbpj3ooVke53L/chs0CO4hqF+q0fsU+KeAC6z5LTaiq4Rs2bqr2l46Yn1x+QEdlmCIQe1MhnySZFZziqnB6cmRboxmzqW+G+hOxjmsyws/d5rncoD7HSv/BBlGNNywCr25vYWWlLHwntziYuYWDdLhMExnQP2kpWkNvcZEVEyN+gPjW9ThBm9cEohQpxk3bda0za3fHpl2nEdVoKkIZl4PkrIbRKZZmky2v5YqohX3XwIDAQAB";
	// 沙箱接口路径
	public static String GATEWAY_URL = " https://openapi.alipaydev.com/gateway.do";
	public static  String FORMAT = "JSON";
	// 签名方式
	public static String SIGN_TYPE = "RSA2";
	// 支付宝异步通知路径,付款完毕后会异步调用本项目的方法,必须为公网地址
	public static String NOTIFY_URL = "http://127.0.0.1:9105/notifyUrl.do";
	// 支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
	public static String RETURN_URL = "http://localhost:9105/returnUrl.do";
}
