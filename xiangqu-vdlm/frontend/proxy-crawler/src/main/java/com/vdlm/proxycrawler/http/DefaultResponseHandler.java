package com.vdlm.proxycrawler.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import com.vdlm.proxycrawler.utils.Statics;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 2:09:46 PM Jul 19, 2014
 */
class DefaultResponseHandler implements ResponseHandler<HttpInvokeResult> {
	@Override
	public HttpInvokeResult handleResponse(final HttpResponse response) {
		final HttpInvokeResult result = new HttpInvokeResult();
		result.setResponse(response);
		try {
			final StatusLine statusLine = response.getStatusLine();
			final HttpEntity entity = response.getEntity();

			result.setStatusCode(statusLine.getStatusCode());

			if (statusLine.getStatusCode() >= 300) {
				result.setReason(statusLine.getReasonPhrase());
				EntityUtils.consume(entity);
			}
			result.setCharset(this.getCharset(entity));
			result.setContent(this.readBytes(entity));
		}
		catch (final Throwable e) {
			result.setReason(e.getMessage());
			result.setException(e);
		}

		return result;
	}

	Charset getCharset(final HttpEntity entity) {
		final ContentType contentType = ContentType.getOrDefault(entity);
		Charset charset = contentType.getCharset();
		if (charset == null) {
			charset = Charset.forName(Statics.ENCODE);
		}
		return charset;
	}

	byte[] readBytes(final HttpEntity entity) throws IOException {
		final InputStream instream = entity.getContent();
		if (instream == null) {
			return null;
		}
		try {
			int i = (int) entity.getContentLength();
			if (i < 0) {
				i = 4096;
			}
			final ByteArrayBuffer buffer = new ByteArrayBuffer(i);
			final byte[] tmp = new byte[1024];
			int l;
			while ((l = instream.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
			return buffer.toByteArray();
		}
		finally {
			instream.close();
		}
	}
}
