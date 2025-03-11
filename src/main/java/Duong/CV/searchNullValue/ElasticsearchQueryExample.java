package Duong.CV.searchNullValue;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetricAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class để thực hiện truy vấn Elasticsearch với aggregation, bao gồm bucket "unknown" cho updatedBy.firstName.
 */
public class ElasticsearchQueryExample {

    private static final String HOST = "localhost";
    private static final int PORT = 9200;
    private static final String SCHEME = "http";
    private static final String INDEX_NAME = "s_ft_5050000_ibtapczd"; // Thay bằng index thực tế

    public static void main(String[] args) {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(HOST, PORT, SCHEME)))) {

            SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

            // Aggregation cho các trạng thái hiện có (giả định trường là at_50)
            TermsAggregationBuilder termsAgg = AggregationBuilders.terms("aggr")
                    .field("at_50.keyword") // Thay bằng trường trạng thái thực tế nếu không phải at_50
                    .size(10);

            // Đếm số lượng updatedBy.firstName là null, rỗng, hoặc "unknown"
            ScriptedMetricAggregationBuilder unknownAgg = AggregationBuilders.scriptedMetric("unknown_count")
                    .initScript(new Script("state.unknown = 0"))
                    .mapScript(new Script(
                            "def firstName = doc['updatedBy.firstName.keyword'].size() > 0 ? doc['updatedBy.firstName.keyword'][0] : null; " +
                                    "if (firstName == null || firstName == '' || firstName.toLowerCase() == 'unknown') { state.unknown += 1; }"
                    ))
                    .combineScript(new Script("return state.unknown"))
                    .reduceScript(new Script("def total = 0; for (s in states) { total += s; } return total"));

            // Thiết lập nguồn tìm kiếm
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.aggregation(termsAgg);
            searchSourceBuilder.aggregation(unknownAgg);
            searchSourceBuilder.size(0); // Chỉ lấy aggregation, không lấy hits
            searchRequest.source(searchSourceBuilder);

            // Thực thi truy vấn
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // Xử lý kết quả
            ParsedStringTerms termsAggregation = searchResponse.getAggregations().get("aggr");
            long unknownCount = (Long) searchResponse.getAggregations().get("unknown_count").getProperty("value");

            // Tạo danh sách bucket tùy chỉnh
            List<Map<String, Object>> customBuckets = new ArrayList<>();
            for (ParsedStringTerms.ParsedBucket bucket : termsAggregation.getBuckets()) {
                Map<String, Object> bucketMap = new HashMap<>();
                bucketMap.put("key", bucket.getKeyAsString());
                bucketMap.put("count", bucket.getDocCount());
                customBuckets.add(bucketMap);
            }

            // Thêm bucket "unknown" vào danh sách
            Map<String, Object> unknownBucket = new HashMap<>();
            unknownBucket.put("key", "unknown");
            unknownBucket.put("count", unknownCount);
            customBuckets.add(unknownBucket);

            // Tạo JSON output tùy chỉnh
            Map<String, Object> customAggr = new HashMap<>();
            customAggr.put("sumOtherDocCount", termsAggregation.getSumOfOtherDocCounts());
            customAggr.put("buckets", customBuckets);
            customAggr.put("value", null);

            Map<String, Object> customResponse = new HashMap<>();
            customResponse.put("totalHits", searchResponse.getHits().getTotalHits().value);
            customResponse.put("tookInMillis", searchResponse.getTook().getMillis());
            customResponse.put("hits", new ArrayList<>());
            customResponse.put("aggregations", new HashMap<String, Object>() {{
                put("aggr", customAggr);
            }});
            customResponse.put("error", null);

            // In kết quả
            System.out.println(customResponse);

        } catch (IOException e) {
            System.err.println("Lỗi khi kết nối hoặc thực thi truy vấn Elasticsearch: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
        }
    }
}