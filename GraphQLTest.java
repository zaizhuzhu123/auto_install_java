package com.laien.cms;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import okhttp3.*;
import org.mountcloud.graphql.GraphqlClient;
import org.mountcloud.graphql.request.mutation.DefaultGraphqlMutation;
import org.mountcloud.graphql.request.mutation.GraphqlMutation;
import org.mountcloud.graphql.request.param.RequestObjectParameter;
import org.mountcloud.graphql.request.query.DefaultGraphqlQuery;
import org.mountcloud.graphql.request.query.GraphqlQuery;
import org.mountcloud.graphql.request.result.ResultAttributtes;
import org.mountcloud.graphql.response.GraphqlResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GraphQLTest {


    private static OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(10, 5L, TimeUnit.MINUTES))
                .build();
    }


    public static void main(String[] args) {
        //leo's key
        String leoKey = "C0LHZ12E7KGJUC9ECDKI7O16WHU1M1GER7WNZ0HU";
        //kevin's key
        String kevinKey = "84R3KUS0YZ9JQ5TTHQTJWI8Y8KSTO2IAR6B8AUXF";


//        String result = getPod(leoKey);
//        System.out.println(getGpuTypes(leoKey));
//        System.out.println(removePod(leoKey, "ktsdzphle9ieoa"));
//        System.out.println(createPod(leoKey, ""));
        System.out.println(getPod(leoKey));
//        System.out.println(getPod(kevinKey));
//        System.out.println(getGpuTypes(leoKey));
//        System.out.println(startPod(kevinKey, "2gykyyg1qyozl1"));
//        System.out.println(startPod(leoKey, "nfgsd3kjw0rqb6"));
//        System.out.println(startPod(leoKey, "9eqqw8fyf63ht9"));
//        System.out.println(stopPod(leoKey, "nfgsd3kjw0rqb6"));

//        System.out.println(removePod(leoKey, "j0z8g4se4tkaug"));

        System.exit(0);
    }

    /**
     * 销毁实例
     *
     * @param apiKey
     * @param podId
     * @return
     */
    private static String removePod(String apiKey, String podId) {
        String jsonFormat = "{\"query\":\"\\nmutation {\\n podTerminate(\\n input: {podId: \\\"" + podId + "\\\"}\\n )\\n}\"}";
        String url = "https://api.runpod.io/graphql?api_key=" + apiKey;
//        // 设置请求内容
//        GraphqlClient client = GraphqlClient.buildGraphqlClient("https://api.runpod.io/graphql?api_key=" + apiKey);
//        GraphqlMutation mutation = new DefaultGraphqlMutation("podTerminate");
//        Map<String, Object> inputMap = Maps.newHashMap();
//        inputMap.put("podId", podId);
//        RequestObjectParameter input = new RequestObjectParameter(inputMap);
//        mutation.addParameter("input", input);
//        mutation.addResultAttributes("id", "desiredStatus");
        // 发起请求
        try {
//            GraphqlResponse response = client.doMutation(mutation);
//            Map data = response.getData();
//            return JSON.toJSONString(data);
            return postJson(url, jsonFormat);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }


    /**
     * 创建实例 并自动启动java程序
     * @param apiKey
     * @param dockerArgs
     * @return
     */
    private static String createPod(String apiKey, String dockerArgs) {
        String url = "https://api.runpod.io/graphql?api_key=" + apiKey;
//        GraphqlClient client = GraphqlClient.buildGraphqlClient("https://api.runpod.io/graphql?api_key=" + apiKey);
        String jsonFormat = "{\"query\":\"\\nmutation {\\n podFindAndDeployOnDemand(\\n input: {cloudType: ALL, gpuCount: 1, volumeInGb: 40, containerDiskInGb: 40, minVcpuCount: 2, minMemoryInGb: 15, gpuTypeId: \\\"NVIDIA RTX A5000\\\", name: \\\"RunPod Tensorflow_test\\\", imageName: \\\"runpod/stable-diffusion:web-automatic-1.5\\\", dockerArgs: \\\"bash -c \\\\\\\"apt update;apt install -y wget;DEBIAN_FRONTEND=noninteractive apt-get install openssh-server -y;mkdir -p ~/.ssh;cd $_;chmod 700 ~/.ssh;echo ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIMxUdLvYhWLTzj+g5O2DqZXLqDTeyijmlgNYLhciYQ42 yuanwang@dahemadeMacBook-Pro.local > authorized_keys;chmod 700 authorized_keys;service ssh start;cd ~/;wget https://raw.githubusercontent.com/zaizhuzhu123/auto_install_java/main/jdk_install.sh;chmod +x ./jdk_install.sh;./jdk_install.sh;source /etc/profile;sleep infinity\\\\\\\"\\\", ports: \\\"8888/http,22/tcp,8889/tcp\\\", volumeMountPath: \\\"/workspace\\\", env: [{key: \\\"JUPYTER_PASSWORD\\\", value: \\\"laien123456\\\"}]}\\n ) {\\n id\\n imageName\\n env\\n machineId\\n machine {\\n podHostId\\n }\\n }\\n}\"}";
        // 发起请求
        try {
//            System.out.println(JSON.toJSONString(mutation));
//            GraphqlResponse response = client.doMutation(mutation);
//            Map data = response.getData();
//            return JSON.toJSONString(data);
            return postJson(url, jsonFormat);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }


    private static String postJson(String url, String json) {
        MediaType parse = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(parse, json);
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url(url)
                .post(requestBody).build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.code() == 200) {
                return response.body().string();
            } else {
                throw new RuntimeException("postJson fail,response Code=" + response.code() + ",msg=" + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }


    /**
     * 停止实例
     * @param apiKey
     * @param podId
     * @return
     */
    public static String stopPod(String apiKey, String podId) {
        String url = "https://api.runpod.io/graphql?api_key=" + apiKey;
        // 设置请求内容
        GraphqlMutation mutation = new DefaultGraphqlMutation("podStop");
        Map<String, Object> inputMap = Maps.newHashMap();
        inputMap.put("podId", podId);
        RequestObjectParameter input = new RequestObjectParameter(inputMap);
        mutation.addParameter("input", input);
        mutation.addResultAttributes("id", "desiredStatus");
        // 发起请求
        try {
//            GraphqlResponse response = client.doMutation(mutation);
//            Map data = response.getData();
//            return JSON.toJSONString(data);
            return postJson(url, mutation.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    /**
     * 重启实例
     * @param apiKey
     * @param podId
     * @return
     */
    public static String startPod(String apiKey, String podId) {
        //There are not enough free GPUs on the host machine to start this pod
        String url = "https://api.runpod.io/graphql?api_key=" + apiKey;
//        GraphqlClient client = GraphqlClient.buildGraphqlClient("https://api.runpod.io/graphql?api_key=" + apiKey);
////        // 设置 http header
////        Map httpHeaders = new HashMap<>();
////        httpHeaders.put("Content-Type", "application/json");
////        client.setHttpHeaders(httpHeaders);
        // 设置请求内容
        GraphqlMutation mutation = new DefaultGraphqlMutation("podResume");
        Map<String, Object> inputMap = Maps.newHashMap();
        inputMap.put("podId", podId);
        inputMap.put("gpuCount", 1);
        RequestObjectParameter input = new RequestObjectParameter(inputMap);
        mutation.addParameter("input", input);
        mutation.addResultAttributes("id", "desiredStatus", "imageName", "env", "machineId");
        //machine
        ResultAttributtes machine = new ResultAttributtes("machine");
        mutation.addResultAttributes(machine);
        machine.addResultAttributes("podHostId");
        // 发起请求
        try {
//            GraphqlResponse response = client.doMutation(mutation);
//            Map data = response.getData();
//            return JSON.toJSONString(data);
            return postJson(url, mutation.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    /**
     * 获取gpu类型
     * @param apiKey
     * @return
     */
    public static String getGpuTypes(String apiKey) {
        String url = "https://api.runpod.io/graphql?api_key=" + apiKey;
        // 设置请求内容
        GraphqlQuery query = new DefaultGraphqlQuery("gpuTypes");
        query.addResultAttributes("id", "displayName", "memoryInGb");
        // 发起请求
        try {
            return postJson(url, query.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    /**
     * 获取用户实例信息
     * @param apiKey
     * @return
     */
    public static String getPod(String apiKey) {
        String url = "https://api.runpod.io/graphql?api_key=" + apiKey;
//        GraphqlClient client = GraphqlClient.buildGraphqlClient(url);
        // 设置 http header
//        Map httpHeaders = new HashMap<>();
//        httpHeaders.put("Content-Type", "application/json");
//        client.setHttpHeaders(httpHeaders);
        // 设置请求内容
        GraphqlQuery query = new DefaultGraphqlQuery("myself");
        //pods
        ResultAttributtes pods = new ResultAttributtes("pods");
        query.addResultAttributes(pods);
        pods.addResultAttributes("id", "name", "containerRegistryAuthId", "dockerId", "machineId", "consumerUserId");
        //runtime
        ResultAttributtes runtime = new ResultAttributtes("runtime");
        runtime.addResultAttributes("uptimeInSeconds");
        pods.addResultAttributes(runtime);
        //registry
        ResultAttributtes registry = new ResultAttributtes("registry");
        registry.addResultAttributes("auth", "pass", "url", "user", "username");
        pods.addResultAttributes(registry);
        //machine
        ResultAttributtes machine = new ResultAttributtes("machine");
        machine.addResultAttributes("location", "podHostId", "note");
        pods.addResultAttributes(machine);
        //
        //ports
        ResultAttributtes ports = new ResultAttributtes("ports");
        ports.addResultAttributes("ip", "isIpPublic", "privatePort", "publicPort", "type");
        runtime.addResultAttributes(ports);
        //gpus
        ResultAttributtes gpus = new ResultAttributtes("gpus");
        gpus.addResultAttributes("id", "gpuUtilPercent", "gpuUtilPercent");
        runtime.addResultAttributes(gpus);
        //container
        ResultAttributtes container = new ResultAttributtes("container");
        container.addResultAttributes("cpuPercent", "memoryPercent");
        runtime.addResultAttributes(container);
        // 发起请求
        try {
            return postJson(url, query.toString());
//            GraphqlResponse response = client.doQuery(query);
//            Map data = response.getData();
//            return JSON.toJSONString(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }
}
