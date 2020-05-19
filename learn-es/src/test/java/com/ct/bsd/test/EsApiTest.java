package com.ct.bsd.test;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class EsApiTest {


    private final static String RECOMMEND_INDEX = "recommend";
    private final static String MY_INDEX = "liyuzhi";
    private final static String RECOMMEND_TYPE = "Movie";
    private final static String MY_TYPE = "Movie";


    @Test
    public void getClient() {

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 地理位置的查询
     */
    @Test
    public void geoQueryTest() {

    }


    /**
     * 嵌套查询API测试，应用场景为在同一条数据中出现嵌套对象，该对象的形式是以字段对应数组的形式存储
     * 需要注意的是这种查询需要在mapping映射中定义好
     */
    @Test
    public void nestedTest() throws Exception {
        TransportClient client;
        /**
         * ScoreMode.Avg：查询到的嵌套对象的合并分数的方式
         * obj：嵌套对象的字段，这个字段在mapping映射需要设定
         */
        QueryBuilders.nestedQuery("obj", QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("", "")), ScoreMode.Avg);
    }


    /**
     * 将已存在的数据从一个索引复制到另一个索引，需要注意的是索引信息不会被赋值，新的索引用的是默认的索引信息
     */

    @Test
    public void reindexTest() throws Exception {
        TransportClient client;
        ReindexRequestBuilder destination = ReindexAction.INSTANCE.newRequestBuilder(null).source("liyuzhi")
                .destination("liyuzhi1");

        BulkByScrollResponse response = destination.get();


    }


    //根据查询出来的结果进行更新操作
    @Test
    public void updateByQueryRequestTest() throws Exception {
        TransportClient client = null;
        String script = "if(ctx._source.name=='liyuzhixxxx'){ctx.op='delete'} else{ctx.op='noop'}";

        //abortOnVersionConflict设置false，表示当版本冲突的时候会不会终止程序
        UpdateByQueryRequestBuilder queryRequestBuilder = UpdateByQueryAction.INSTANCE.newRequestBuilder(client)
                .abortOnVersionConflict(false).source("liyuzhi")
                //设置查询条件，根据查询条件进行更新
                //.filter(QueryBuilders.matchQuery("suggest", "suggest3"))
                .script(new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, script,
                        Collections.emptyMap()));
        BulkByScrollResponse bulkByScrollResponse = queryRequestBuilder.get();


    }


    //使用批量处理处理器来处理多个文档
    @Test
    public void bulkProcess() throws Exception {

        BulkProcessor.Builder builder = BulkProcessor.builder(null, new BulkProcessor.Listener() {
            //该方法在执行批处理之前执行
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {

            }

            //该方法在执行批处理之后执行
            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {

            }

            //该方法在执行批处理后抛出异常的时候执行
            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {

            }
        });
        BulkProcessor bulkProcessor = builder.build();

        //当操作数达到10000个操作，就会执行一次批处理，默认的是1000
        builder.setBulkActions(10000);
        //五秒的间隔执行一次bulk操作，默认不开启
        builder.setFlushInterval(TimeValue.timeValueSeconds(5));
        //设置没达到5M的请求大小就执行一次bulk操作，默认是5MB
        builder.setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setConcurrentRequests(1)
                .setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueMinutes(100), 3));//设置批处理的线程数，默认是0,一个线程

        //设置具体的操作请求，例如对指定的文档做操作

        bulkProcessor.add(new IndexRequest("索引名", "类型", "文档ID")
                .source(XContentFactory.jsonBuilder().startObject().field("字段", "字段值").endObject()));


        //关闭批处理,1秒后关闭
        bulkProcessor.awaitClose(1000, TimeUnit.MILLISECONDS);
        //这样关闭也可以
        bulkProcessor.close();


    }


    //Bulk API可以利用一个去创建多个或者删除多个文档。
    @Test
    public void bulkAPI() throws Exception {
        TransportClient client = null;

        BulkRequestBuilder prepareBulk = client.prepareBulk();

        //新建索引
        BulkRequestBuilder requestBuilder = prepareBulk.add(client
                .prepareIndex("liyuzhi", "test", "AWiKSqVdfvdjJWfsMSK4")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("name", "chenjie")
                        .field("age", 1000000)
                        .field("suggest", "suggest000000000000")
                        .endObject()));
        //删除索引
        BulkRequestBuilder requestBuilder1 = prepareBulk
                .add(client.prepareDelete("liyuzhi", "test", "AWiKSdk6fvdjJWfsMSKx"));

        //通过一个请求 操作多个文档
        BulkResponse bulkResponse = prepareBulk.get();


    }


    //搜索多个不同索引上的文档，可以是不同的索引，不同文档ID
    @Test
    public void multiGetAPITest() throws Exception {
        TransportClient client = null;

        MultiGetResponse multiGetResponse = client.prepareMultiGet()
                .add("liyuzhi", "test", "AWiKSdk6fvdjJWfsMSKx")
                .add("recommend", "Movie", "1699")
                .get();

        for (MultiGetItemResponse multiGetItemResponse : multiGetResponse.getResponses()) {
            GetResponse getResponse = multiGetItemResponse.getResponse();
            String index = getResponse.getIndex();

            Optional.ofNullable(getResponse).ifPresent(response -> {
                if (Objects.equals(index, MY_INDEX)) {

                } else {

                }
            });
        }
    }


    //ES更新测试，该API更新文档的时候，如果文档存在就正常更新，如果文档不存在就新增该文档
    @Test
    public void upSertTest() throws Exception {
        TransportClient client = null;
        //创建一个IndexRequest
        IndexRequest indexRequest = new IndexRequest("liyuzhi", "test", "AWiKSdk6fvdjJWfsMSK8");

        //如果不存在就会走这个API
        indexRequest.source(XContentFactory.jsonBuilder()
                .startObject()
                .field("name", "xxxxxxx")
                .field("address", "营口大石桥")
                .endObject());


        //创建一个UpdateRequest
        UpdateRequest updateRequest = new UpdateRequest("liyuzhi", "test", "AWiKSdk6fvdjJWfsMSK8");
        //如果存在就会走着API
        updateRequest.doc(XContentFactory.jsonBuilder().startObject().field("name", "wxm1111111").endObject());
        //设置更新的类型为upsert
        updateRequest.upsert(indexRequest);


        client.update(updateRequest).get();


    }


    //更新测试
    @Test
    public void updateTest() throws Exception {
        TransportClient client = null;
        //创建一个UpdateRequest
        UpdateRequest updateRequest = new UpdateRequest();
        //指定要更新的索引
        updateRequest.index("liyuzhi");
        //设置更新的文档类型
        updateRequest.type("test");
        //设置更新的问道ID
        updateRequest.id("AWiKSdk6fvdjJWfsMSK9");

        //设置更新文档的哪个字段
        updateRequest.doc(XContentFactory.jsonBuilder().startObject().field("name", "wxm").endObject());

        //利用update方法更新
        client.update(updateRequest).get();


    }


    //监听删除结果状态，失败或者成功
    @Test
    public void deleteByQueryListener() throws Exception {

        DeleteByQueryAction.INSTANCE.newRequestBuilder(null)
                .filter(QueryBuilders.termQuery("field", "value"))
                .source("index")
                .execute(new ActionListener<BulkByScrollResponse>() {
                    @Override
                    public void onResponse(BulkByScrollResponse bulkByScrollResponse) {
                        //删除成功执行此方法,返回删除文档的数量
                        long deleted = bulkByScrollResponse.getDeleted();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        //删除失败执行此方法
                    }
                });


    }


    //根据查询API查询出来的文档集合进行删除
    @Test
    public void deleteByQuery() throws Exception {
        BulkByScrollResponse response = DeleteByQueryAction
                .INSTANCE.newRequestBuilder(null)
                .filter(QueryBuilders.termQuery("field", "value"))//查询条件
                .source("index")//索引名
                .get();
        //别删除文档的数量
        long deleteId = response.getDeleted();

    }


    //删除API测试
    @Test
    public void deleteAPITest() throws Exception {
        TransportClient client = null;
        //根据索引、类型、文档Id删除文档
        client.prepareDelete("index", "type", "documentId");
    }


    //get测试
    @Test
    public void getAPITest() {
        try {
            TransportClient client = null;
            //根据索引、类型、文档ID获取对应的文档
            client.prepareGet("index", "type", "documentId");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //聚合查询测试
    @Test
    public void aggregateTest() throws Exception {

        TransportClient client = null;

        /**
         *
         * 指定对哪个字段进行聚合检索，需要注意的是，如果对着字段进行聚合检索，那么该字段的mapping映射的类型必须是keyword类型
         *
         * 并且指定聚合的name，
         */
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders
                .terms("clazz")
                .field("clazz")
                /**
                 *  对聚合结果进行排序，按照age_avg进行排序，age_avg是我们自己在avg方法里定义的Avg聚合名称
                 *  根据这个名称我们可以拿到聚合后的结果。
                 */
                .order(Terms.Order.aggregation("age_avg", true));

        //多条件聚合进行字段值的统计，类似于select clazz,age,name,count(*) from table group by clazz,age,name
        aggregationBuilder.subAggregation(AggregationBuilders.terms("age")
                .field("age")
                .subAggregation(AggregationBuilders.terms("name").field("name"))
        );

        //获取分组中年龄最大的
        aggregationBuilder.subAggregation(AggregationBuilders.max("maxage").field("age"));

        //聚合操作，获取每个班级的年龄的总和平均年龄
        aggregationBuilder.subAggregation(AggregationBuilders.sum("age_sum").field("age"))
                .subAggregation(AggregationBuilders.avg("age_avg").field("age"));


        SearchRequestBuilder requestBuilder = client.prepareSearch("liyuzhi")
                .setTypes("test")
                //添加聚合构建对象
                .addAggregation(aggregationBuilder);
        SearchResponse response = requestBuilder.get();

        //根据聚合名字获取封装聚合结果的对象
        StringTerms terms = response.getAggregations().get("clazz");


        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        /**
         * s.getKey：字段值
         * s.getDocCount：字段值对应的聚合结果
         */
        buckets.stream().map(s -> (Terms.Bucket) s).forEach(s -> {
            System.out.println("班级=" + s.getKey());
            Terms ageTerms = s.getAggregations().get("age");

            //获取聚合后的年龄求和结果
            Sum max = s.getAggregations().get("age_sum");
            //获取聚合后的平均年龄结果
            Avg avg = s.getAggregations().get("age_avg");

            Integer integer = Integer.parseInt("111");


            System.out.println(max.getName() + "---" + max.getValue());
            System.out.println(avg.getName() + "---" + avg.getValue());
            ageTerms.getBuckets()
                    .stream()
                    .map(ageTerm -> (Terms.Bucket) ageTerm)
                    .forEach(term -> {
                        System.out.println("年龄=" + term.getKey() + ",个数=" + term.getDocCount());
                        Terms nameTerms = term.getAggregations().get("name");
                        for (Terms.Bucket bucket : nameTerms.getBuckets()) {
                            System.out.println("名字=" + bucket.getKey() + ",个数=" + bucket.getDocCount());
                        }
                    });
        });
    }


    //搜索词自动补全查询测试
    @Test
    public void autoCompletionSuggestTest() throws Exception {
        TransportClient client = null;
        //自动补全关键字前缀
        String prifex = "auto";
        //指定自动补全的字段，并且指定根据前缀自动补全还是根据其他位置补全，并且指定自动补全候选词的个数。
        CompletionSuggestionBuilder suggestionBuilder = SuggestBuilders
                //指定在哪个字段上获取补全信息，该字段的类型必须是completion类型
                .completionSuggestion("suggest")
                //根据前缀去获取补全信息
                .prefix("this")
                //获取补全信息的最大条数为10条
                .size(10);

        //定义一个执行补全信息的规则
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        //添加一个补全信息
        suggestBuilder.addSuggestion("autocompletion", suggestionBuilder);
        SearchRequestBuilder requestBuilder = client.prepareSearch("liyuzhi")
                .setTypes("test")
                .suggest(suggestBuilder);
        SearchResponse response = requestBuilder.get();
        Suggest suggest = response.getSuggest();
        //根据补全的名字获取封装了补全信息的Suggest
        Suggest.Suggestion result = suggest.getSuggestion("autocompletion");
        //获取自动补全的条目信息
        List entries = result.getEntries();
        List<String> suggestNames = new ArrayList<>();
        for (Object entry : entries) {
            if (entry instanceof CompletionSuggestion.Entry) {
                CompletionSuggestion.Entry suggestEntry = (CompletionSuggestion.Entry) entry;

                //所有的补全信息集合
                List<CompletionSuggestion.Entry.Option> options = suggestEntry.getOptions();
                if (options.isEmpty()) {
                    continue;
                }
                options.stream().map(option -> option.getText().toString()).distinct().limit(5).forEach(suggestNames::add);

            }
        }
        suggestNames.forEach(s -> System.out.println(s));
    }


    //匹配查询
    private void matchQueryTest() {
        //利用字段名与字段值去匹配文档
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("filename", "value");
    }

    //匹配查询
    private void matchAllQueryTest() {
        //匹配所有文档API
        MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    }


    //使用复合查询测试
    private void boolQueryBuilderTest() {
        try {
            TransportClient client = null;
            /**
             * 使用复合查询,具体的条件API有must、mustNot、should
             * must：相当于SQl中的and，
             * should：相当于SQL中的or
             * mustNot：相当于不等于
             */
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            /**
             * es中默认的字符串是分词的以及被分析的，也就是说一个词被拆分成多个词，并且这个词会被转换成小写，
             * termQuery：是精确查找，搜索前不会对搜索词进行分词与分析处理，如果想搜索结果我们可以把搜索词语转换成小写，我们也可以搜索到被分词的包含搜索词的相关文档
             * matchQuery：在搜索之前会对搜索词进行分词与分析，一个搜索词被分词，并且被转成小写，ES默认会将数据做分词分析处理，所以我们可以用match匹配文档，不受大小写影响。
             *
             */

            boolQuery.filter(QueryBuilders.termsQuery("shoot", "2004"))
                    .should(QueryBuilders.termsQuery("name", "king"))
                    .mustNot(QueryBuilders.termsQuery("name", "along"))
                    .filter(QueryBuilders.rangeQuery("").lte(100).gte(1000));
            /**
             * keywords：搜索关键词
             * field1...：多个字段，利用搜索关键词在这些字段中检索
             * 该检索API的作用就是对多个字段做相同搜索词的检索，
             */
            boolQuery.must(QueryBuilders.multiMatchQuery("keywords", "field1", "field2", "field3"));

            SearchResponse response = client.prepareSearch(RECOMMEND_INDEX).setTypes(RECOMMEND_TYPE)
                    .setQuery(boolQuery)
                    /**
                     * 改API方法的作用就是用于检索大批量数据用的，例如正常我们查询的数据不能多于10000条，那么我们
                     * 可以用下边的API方法去滚动查询，
                     */
                    .setScroll(new TimeValue(2000))
                    /**
                     * 该API方法指定了搜索类型，共有四种搜索类型，query and fatch、query then fatch、dfs query and fatch、dfs query then fatch
                     * 默认的是query then fatch
                     *
                     * query and fatch：向每个分片发送请求，然后每个分片将对应的文档和排序信息返回给客户端，然后客户端在将这些文档做全局排序。
                     *                  优点：快，因为这种方式只与分片交互一次。
                     *                  缺点：排序不准确、返回结果数量不准确
                     *
                     * query then fatch(默认)：向每个分片发送请求，然后每个分片将文档Id和排序信息返回给客户端，客户端对结果进行全局排序，然后根据排序结果再次向
                     *                   分片去获取文档的详细信息，然后客户端将文档信息返回
                     *                   优点：数据量准确
                     *                   缺点：性能一般，排序结果不准确
                     *
                     * dfs query and fatch：在搜索之前先向所有分片发送请求，将所有分片中参与排序的相关信息汇总到一起，然后每个分片上的排序以汇总结果为依据进行排序，
                     *                      然后将将排序的结果返给客户端。
                     *                      优点：排序准确
                     *                      缺点：性能一般，数据数量不准确，可能是分片*n个结果
                     *
                     * dfs query then fatch：在搜索之前向所有分片发送请求，将所有分片中参与排序的信息汇总到一起，然后每个分片按照汇总结果进行排序，然后将排序后的
                     *                       文档ID和排序信息返回给客户端，然后客户端再将各个分片返回来的结果再次进行排序，然后再根据文档ID到对应分片上去检索文档。
                     *                       优点：排序、数量都准确
                     *                       缺点：性能最差
                     *
                     *
                     *
                     */
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    /**
                     * 在ES5.x以后，ES对字段的排序和聚合需要采用了特定的数据结构fielddata，这种数据结构类型的字段会默认缓存到内存中，
                     * 但是字段类型为text的时候fielddata数据结构默认是禁用的，所以如果不在mapping映射中指定字段的fielddata为true的话，就会出现异常。
                     * 解决办法
                     * {
                     *   "properties": {
                     *         "name": {
                     *             "type": "text",
                     *             "fielddata": true
                     *         }
                     *     }
                     *  }'
                     */
                    .addSort("name", SortOrder.DESC)
                    /**
                     * 该API表示想要获取ES中那些字段，并且排除哪些字段
                     */
                    //setFetchSource(new String[]{"include"}, new String[]{"exclude"})
                    .get();


            //判断响应码是否是成功的
            if (response.status() == RestStatus.OK) {
                for (SearchHit hit : response.getHits()) {
                    Map<String, Object> source = hit.getSource();
                    System.err.println(source.get("name"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}