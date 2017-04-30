package com.midburn.gate.midburngate;

import okhttp3.Response;

public interface HttpRequestListener {

	void onResponse(Response response);
}
