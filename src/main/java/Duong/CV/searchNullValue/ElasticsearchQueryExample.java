package Duong.CV.searchNullValue;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.IOException;

public class ElasticsearchQueryExample {

    public static void main(String[] args) {
        // Create the client
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")))) { // Adjust the host and port accordingly

            // Create the SearchRequest
            SearchRequest searchRequest = new SearchRequest("your-index-name"); // Replace "your-index-name" with your actual index name

            // Build the aggregation query
            TermsAggregationBuilder termsAgg = AggregationBuilders.terms("aggr")
                    .field("at_5012000_diameter.ca_10502.keyword")
                    .size(10)
                    .order(BucketOrder.aggregation("myAggr2", false))
                    .subAggregation(AggregationBuilders.sum("myAggr2")
                            .field("at_5012002"));

            // Setting the source
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.aggregation(termsAgg);
            searchSourceBuilder.size(0); // No hits, only aggregations
            searchRequest.source(searchSourceBuilder);

            // Execute the search
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // Print the response
            System.out.println(searchResponse.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

