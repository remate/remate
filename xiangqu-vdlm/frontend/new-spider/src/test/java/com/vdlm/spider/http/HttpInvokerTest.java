package com.vdlm.spider.http;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.vdlm.config.ApplicationConfig;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.DescTaskBean;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.bean.SkuTaskBean;
import com.vdlm.spider.task.helper.DescParseHelper;
import com.vdlm.spider.task.helper.ItemParseHelper;

/**
 *
 * @author: chenxi
 */

@ActiveProfiles("dev")
@ContextConfiguration(classes = { HttpConfig.class, ApplicationConfig.class })
public class HttpInvokerTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private HttpClientProvider httpClientProvider;
	
	@Test
	public void testTaobaoParseDesc() {
		final String itemId = "40356611485";
		final String itemUrl = "http://item.taobao.com/item.htm?id=40356611485";
		final ItemTaskBean bean = new ItemTaskBean();
		bean.setItemId(itemId);
		bean.setItemUrl(itemUrl);
		bean.setShopType(ShopType.TAOBAO);
		HttpClientInvoker invoker = httpClientProvider.provide(ShopType.TAOBAO, itemUrl);
		HttpInvokeResult result = invoker.invoke();
		final String itemHtml = result.getContentString();
		final String remoteDescUrl = "http:" + ItemParseHelper.getApiDescUrl(bean, itemHtml);
		
		final DescTaskBean descBean = new DescTaskBean();
		bean.setShopType(ShopType.TAOBAO);
		descBean.setRequestUrl(remoteDescUrl);
		invoker = httpClientProvider.provide(remoteDescUrl, invoker);
		result = invoker.invoke();
		final String descHtml = result.getContentStringAndReset();
		final String details = DescParseHelper.formatDesc(descHtml);
		final List<FragmentVO> fragments = DescParseHelper.getDescFragments(descBean, details);
		final List<String> descImgs = DescParseHelper.getImgsFromString(details);
		System.out.println(""); 
	}
	
	@Test
	public void testTmallParseDesc() {
		final String itemId = "45097062434";
		final String itemUrl = "http://detail.tmall.com/item.htm?id=45097062434";
		
		final ItemTaskBean bean = new ItemTaskBean();
		bean.setItemId(itemId);
		bean.setItemUrl(itemUrl);
		bean.setShopType(ShopType.TMALL);
		HttpClientInvoker invoker = httpClientProvider.provide(ShopType.TMALL, itemUrl);
		HttpInvokeResult result = invoker.invoke();
		final String itemHtml = result.getContentString();
		final String remoteDescUrl = "http:" + ItemParseHelper.getApiDescUrl(bean, itemHtml);
		
		final DescTaskBean descBean = new DescTaskBean();
		bean.setShopType(ShopType.TMALL);
		descBean.setRequestUrl(remoteDescUrl);
		invoker = httpClientProvider.provide(ShopType.TMALL, remoteDescUrl);
		result = invoker.invoke();
		final String descHtml = result.getContentStringAndReset();
		final String details = DescParseHelper.formatDesc(descHtml);
		final List<FragmentVO> fragments = DescParseHelper.getDescFragments(descBean, details);
		final List<String> descImgs = DescParseHelper.getImgsFromString(details);
		System.out.println(""); 
	}
	
	@Test
	public void testParseTaobaoSku() {
		final String itemId = "38108100680";
		final String itemUrl = "item.taobao.com/item.htm?id=38108100680";
		ItemTaskBean bean = new ItemTaskBean();
		bean.setItemId(itemId);
		bean.setItemUrl(itemUrl);
		bean.setRequestUrl(itemUrl);
		HttpClientInvoker invoker = httpClientProvider.provide(ShopType.TAOBAO, itemUrl);

		final String skuUrl = "http://detailskip.taobao.com/json/sib.htm?itemId=38108100680&sellerId=27763132&u=1&p=1&rcid=50010404&sts=471429136,1170940490216898628,72409575231357056,13581171924403203&chnl=pc&price=19900&shopId=&vd=1&skil=false&pf=1&al=false&ap=0&ss=0&free=1&st=1&ct=1";
		bean = new SkuTaskBean();
		bean.setItemId(itemId);
//		bean.setSkuUrl(skuUrl);
		bean.setRequestUrl(skuUrl);
		bean.setShopType(ShopType.TAOBAO);
		invoker = httpClientProvider.provide(skuUrl, invoker);
		final HttpInvokeResult result = invoker.invoke();
		final String skuHtml = result.getContentString();
		System.out.println(skuHtml);
	}
	
	@Test
	public void testParseWdetailImg() {
		final String imgUrl = "http://gw.alicdn.com/bao/uploaded/i3/583887120/TB2EYEvbVXXXXXeXpXXXXXXXXXX_!!583887120.jpg";
		final HttpClientInvoker invoker = httpClientProvider.provide(ShopType.TAOBAO, imgUrl);
		final HttpInvokeResult result = invoker.invoke();
		final String imgContent = result.getContentString();
		System.out.println(imgContent);
	}
	
	@Test
	public void testJsoup() {
		final String desc = "<html>\n <head></head>\n <body>\n  <p style=\"text-align: center;\"><span style=\"background-color: #ffffff;\"><span style=\"color: #ff0000;\"></span></span></p>\n  <p style=\"text-align: center;\"><span style=\"background-color: #ffffff;\"><span style=\"color: #ff0000;\"></span></span></p>\n  <p style=\"text-align: center;\"><span style=\"background-color: #ffffff;\"><span style=\"color: #ff0000;\"><span><strong>补邮费，商品差价，专拍连接,是多少拍多少！</strong></span></span></span></p>\n  <p style=\"text-align: center;\"><span style=\"background-color: #ffffff;\"><span style=\"color: #ff0000;\"></span></span></p>\n  <p style=\"text-align: center;\"><span style=\"background-color: #ffffff;\"><span style=\"color: #ff0000;\"><span><strong>拍时请在留言内注明：订单编号及事项</strong></span></span></span></p>\n  <p style=\"text-align: center;\"><span style=\"background-color: #ffffff;\"><span style=\"color: #ff0000;\"></span></span></p>\n  <p style=\"text-align: center;\"><strong>如：订单编号：1234567891012-顺丰补差价</strong></p>\n  <p style=\"text-align: center;\"><span style=\"background-color: #ffffff;\"><span style=\"color: #ff0000;\"></span></span></p>\n  <p style=\"text-align: center;\">&nbsp;</p>\n  <script src=\"http://g-assets.daily.taobao.net/i/popshop/0.0.21/p/seemore/load.js?c\"></script> \n </body>\n</html>";
		final Document doc = Jsoup.parse(desc);
		final Element body = doc.select("body").get(0);
		body.select("script").remove();
		System.out.println(body.html());
	}
	
	@Test
	public void testImg() {
		final String imgUrl = "https://img.alicdn.com/imgextra/i2/496704654/TB2760YdXXXXXbiXXXXXXXXXXXX-496704654.jpg";
		final HttpClientInvoker invoker = httpClientProvider.provide(ShopType.TAOBAO, imgUrl);
		final HttpInvokeResult result = invoker.invoke();
		final String imgContent = result.getContentString();
		System.out.println(imgContent);
	}
	
	@Test
	public void test400() throws Exception {
		while (true) {
			String url = "http://zaofu.tmall.com/";
//			final HttpClient client = new DefaultHttpClient();  
//			client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//			//		httpclient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "GBK");
//
//			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 90000);
//			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 50000);
//
////					if (useProxy) {
////						httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
////					}
//			if (client instanceof DefaultHttpClient) {
//				((DefaultHttpClient) client).setRedirectStrategy(new DefaultRedirectStrategy() {
//					@Override
//					public boolean isRedirected(HttpRequest request, HttpResponse response,
//							HttpContext context) throws ProtocolException {
//						if (AuthUtils.isAuthResponse(response)) {
//							// 需要鉴权，redirect过去也没意义
//							return false;
//						}
//						return super.isRedirected(request, response, context);
//					}
//				});
//			}
//			
//			final HttpGet httpGet = new HttpGet(url);
//			
//			httpGet.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);// 手动处理自动转向.
//			httpGet.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, Boolean.TRUE);
//			httpGet.getParams().setParameter(ClientPNames.MAX_REDIRECTS, 10);
//			
//			final CookieStore cookieStore = new BasicCookieStore();
//			final HttpContext context = new BasicHttpContext();
//	        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
//	        final HttpInvokeResult result = client.execute(httpGet, new DefaultResponseHandler(), context);
//	        System.err.println("status code: " + result.getStatusCode());
			if (url.startsWith("https://")) {
				url = url.replaceFirst("https://", "http://");
			}
			final HttpClientInvoker invoker = httpClientProvider.provide(
					ShopType.TAOBAO, url);
			final HttpInvokeResult result = invoker.invoke();
			System.err.println("status code: " + result.getStatusCode()
					+ " ip:" + invoker.getIp());
			System.err.println("result=" + result.getContentStringAndReset());
		}
	}
	
	@Test
	public void test400Img() {
		while (true) {
			String imgUrl = "http://img02,taobaocdn.com/imgextra/i2/51262845/TB2Jj9dcFXXXXaZXXXXXXXXXXXX_!!51262845.jpg";
			if (imgUrl.startsWith("https://")) {
				imgUrl = imgUrl.replaceFirst("https://", "http://");
			}
			final HttpClientInvoker invoker = httpClientProvider.provide(ShopType.TAOBAO, imgUrl);
			final HttpInvokeResult result = invoker.invoke();
			System.err.println("status code: " + result.getStatusCode() + " ip:" + invoker.getIp());
		}
	}
	
	@Test
	public void test302Item() {
		while (true) {
//			String url = "item.taobao.com/item.htm?id=520812433617";
			String url = "http://shop104184293.taobao.com";
			final HttpClientInvoker invoker = httpClientProvider.provide(ShopType.TAOBAO, url);
			final HttpInvokeResult result = invoker.invoke();
			System.err.println("status code: " + result.getStatusCode() + " ip:" + invoker.getIp());
		}
	}
	
	@Test
	public void testMShop() {
//		final String url = "http://api.m.taobao.com/h5/com.taobao.search.api.getshopitemlist/2.0/?v=2.0&api=com.taobao.search.api.getShopItemList&appKey=12574478&t=1436759678212&callback=mtopjsonp1&type=jsonp&sign=b71f1b43e0a12972cf058768ac9a0003&data={%22shopId%22%3A%22116342376%22%2C%22currentPage%22%3A1%2C%22pageSize%22%3A%2230%22%2C%22sort%22%3A%22oldstarts%22%2C%22q%22%3A%22%22}";
		
		final String url = "http://112815982shop.taobao.com";
		final HttpClientInvoker invoker = httpClientProvider.provide(ShopType.TAOBAO, url);
		final HttpInvokeResult result = invoker.invoke();
		System.err.println("status code: " + result.getStatusCode() + " ip:" + invoker.getIp() + "content:"
				+ result.getContentStringAndReset());
	}
}
