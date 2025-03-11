package Duong.CV.CacheLibrary;

public class Application {
    public static void main(String[] args) {
        CacheService cacheService = new SimpleCacheService();
        cacheService.save("exampleKey", "Hello, World!", 30L); // Lưu trữ với TTL là 30 giây

        // Truy xuất dữ liệu
        System.out.println("Retrieved from cache: " + cacheService.get("exampleKey"));

        // Đợi để kiểm tra dữ liệu sau khi hết hạn TTL
        try {
            Thread.sleep(31000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Retrieved from cache after expiry: " + cacheService.get("exampleKey"));
    }
}

