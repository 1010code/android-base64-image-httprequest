


## OkHttp GET
提供兩種不同寫法。如果要在回傳結果同時渲染更新view，必須要使用 runOnUiThread。

- Call Function
```java
private void getData(){
    /**建立連線*/
    OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
    /**設置傳送需求*/
    Request request = new Request.Builder()
            .url("https://5a279f5c15af.ngrok.io")
            .build();
    /**設置回傳*/
    Call call = client.newCall(request);
    call.enqueue(new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            /**如果傳送過程有發生錯誤*/
            tvResult.setText(e.getMessage());
        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            // updates the UI onto the main thread.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        /**取得回傳*/
                        tvResult.setText("GET回傳：\n" + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    });
}
```

- Thread
```java
new Thread(getThread).start();

public Runnable getThread =new Runnable() {
    public void run() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://5a279f5c15af.ngrok.io")
                .build();
        try {
            /**取得回傳*/
            // updates the UI onto the main thread.
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            Log.e("data", data);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvResult.setText(data);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
};
```

## OkHttp POST

```java
private void postData(String url, String encodedString){
    long startTime = System.nanoTime();
    /**建立連線*/
    OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
    /**設置傳送所需夾帶的內容*/
    FormBody formBody = new FormBody.Builder()
            .add("image", encodedString)
            .build();
    /**設置傳送需求*/
    Request request = new Request.Builder()
            .url(url)
            .post(formBody)
            .build();
    /**設置回傳*/
    Call call = client.newCall(request);
    call.enqueue(new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            /**如果傳送過程有發生錯誤*/
            Log.e("error",e.getMessage());
        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response){
            // updates the UI onto the main thread.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        tvResult.setText("POST回傳：" + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            Log.e("Execute Time ",duration/ 1000000000+" sec");
        }
    });
}
```