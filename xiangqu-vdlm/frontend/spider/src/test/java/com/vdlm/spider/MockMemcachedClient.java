/**
 * 
 */
package com.vdlm.spider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.CASOperation;
import net.rubyeye.xmemcached.Counter;
import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.KeyIterator;
import net.rubyeye.xmemcached.KeyProvider;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientCallable;
import net.rubyeye.xmemcached.MemcachedClientStateListener;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.buffer.BufferAllocator;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.impl.ReconnectRequest;
import net.rubyeye.xmemcached.networking.Connector;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import net.rubyeye.xmemcached.utils.Protocol;

/**
 * <pre>
 *
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 4:01:07 PM Nov 26, 2014
 */
public class MockMemcachedClient implements MemcachedClient {

	@Override
	public void setMergeFactor(int mergeFactor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getConnectTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setConnectTimeout(long connectTimeout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Connector getConnector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOptimizeGet(boolean optimizeGet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOptimizeMergeBuffer(boolean optimizeMergeBuffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isShutdown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addServer(String server, int port) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addServer(InetSocketAddress inetSocketAddress) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addServer(String hostList) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getServersDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeServer(String hostList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBufferAllocator(BufferAllocator bufferAllocator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T get(String key, long timeout, Transcoder<T> transcoder) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T get(String key, long timeout) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T get(String key, Transcoder<T> transcoder) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T get(String key) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> GetsResponse<T> gets(String key, long timeout, Transcoder<T> transcoder) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> GetsResponse<T> gets(String key) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> GetsResponse<T> gets(String key, long timeout) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> GetsResponse<T> gets(String key, Transcoder transcoder) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, T> get(Collection<String> keyCollections, long opTimeout, Transcoder<T> transcoder)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, T> get(Collection<String> keyCollections, Transcoder<T> transcoder) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, T> get(Collection<String> keyCollections) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, T> get(Collection<String> keyCollections, long timeout) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, GetsResponse<T>> gets(Collection<String> keyCollections, long opTime,
			Transcoder<T> transcoder) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, GetsResponse<T>> gets(Collection<String> keyCollections) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, GetsResponse<T>> gets(Collection<String> keyCollections, long timeout)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, GetsResponse<T>> gets(Collection<String> keyCollections, Transcoder<T> transcoder)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> boolean set(String key, int exp, T value, Transcoder<T> transcoder, long timeout)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean set(String key, int exp, Object value) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean set(String key, int exp, Object value, long timeout) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean set(String key, int exp, T value, Transcoder<T> transcoder) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWithNoReply(String key, int exp, Object value) throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void setWithNoReply(String key, int exp, T value, Transcoder<T> transcoder) throws InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> boolean add(String key, int exp, T value, Transcoder<T> transcoder, long timeout)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean add(String key, int exp, Object value) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean add(String key, int exp, Object value, long timeout) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean add(String key, int exp, T value, Transcoder<T> transcoder) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addWithNoReply(String key, int exp, Object value) throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void addWithNoReply(String key, int exp, T value, Transcoder<T> transcoder) throws InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> boolean replace(String key, int exp, T value, Transcoder<T> transcoder, long timeout)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean replace(String key, int exp, Object value) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean replace(String key, int exp, Object value, long timeout) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean replace(String key, int exp, T value, Transcoder<T> transcoder) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void replaceWithNoReply(String key, int exp, Object value) throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void replaceWithNoReply(String key, int exp, T value, Transcoder<T> transcoder)
			throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean append(String key, Object value) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean append(String key, Object value, long timeout) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void appendWithNoReply(String key, Object value) throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean prepend(String key, Object value) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean prepend(String key, Object value, long timeout) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void prependWithNoReply(String key, Object value) throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean cas(String key, int exp, Object value, long cas) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean cas(String key, int exp, T value, Transcoder<T> transcoder, long timeout, long cas)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cas(String key, int exp, Object value, long timeout, long cas) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean cas(String key, int exp, T value, Transcoder<T> transcoder, long cas) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean cas(String key, int exp, CASOperation<T> operation, Transcoder<T> transcoder)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean cas(String key, int exp, GetsResponse<T> getsReponse, CASOperation<T> operation,
			Transcoder<T> transcoder) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean cas(String key, int exp, GetsResponse<T> getsReponse, CASOperation<T> operation)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean cas(String key, GetsResponse<T> getsResponse, CASOperation<T> operation)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean cas(String key, int exp, CASOperation<T> operation) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean cas(String key, CASOperation<T> operation) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> void casWithNoReply(String key, GetsResponse<T> getsResponse, CASOperation<T> operation)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void casWithNoReply(String key, int exp, GetsResponse<T> getsReponse, CASOperation<T> operation)
			throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void casWithNoReply(String key, int exp, CASOperation<T> operation) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void casWithNoReply(String key, CASOperation<T> operation) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean delete(String key, int time) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(String key, long opTimeout) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(String key, long cas, long opTimeout) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touch(String key, int exp, long opTimeout) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touch(String key, int exp) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T getAndTouch(String key, int newExp, long opTimeout) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getAndTouch(String key, int newExp) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<InetSocketAddress, String> getVersions() throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long incr(String key, long delta) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long incr(String key, long delta, long initValue) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long incr(String key, long delta, long initValue, long timeout) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long decr(String key, long delta) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long decr(String key, long delta, long initValue) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long decr(String key, long delta, long initValue, long timeout) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void flushAll() throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flushAllWithNoReply() throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flushAll(long timeout) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flushAll(InetSocketAddress address) throws MemcachedException, InterruptedException, TimeoutException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flushAllWithNoReply(InetSocketAddress address) throws MemcachedException, InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flushAll(InetSocketAddress address, long timeout) throws MemcachedException, InterruptedException,
			TimeoutException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flushAll(String host) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> stats(InetSocketAddress address) throws MemcachedException, InterruptedException,
			TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> stats(InetSocketAddress address, long timeout) throws MemcachedException,
			InterruptedException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<InetSocketAddress, Map<String, String>> getStats(long timeout) throws MemcachedException,
			InterruptedException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<InetSocketAddress, Map<String, String>> getStats() throws MemcachedException, InterruptedException,
			TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<InetSocketAddress, Map<String, String>> getStatsByItem(String itemName) throws MemcachedException,
			InterruptedException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean delete(String key) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Transcoder getTranscoder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTranscoder(Transcoder transcoder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<InetSocketAddress, Map<String, String>> getStatsByItem(String itemName, long timeout)
			throws MemcachedException, InterruptedException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getOpTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOpTimeout(long opTimeout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<InetSocketAddress, String> getVersions(long timeout) throws TimeoutException, InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<InetSocketAddress> getAvaliableServers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<InetSocketAddress> getAvailableServers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addServer(String server, int port, int weight) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addServer(InetSocketAddress inetSocketAddress, int weight) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteWithNoReply(String key, int time) throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteWithNoReply(String key) throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void incrWithNoReply(String key, long delta) throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decrWithNoReply(String key, long delta) throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLoggingLevelVerbosity(InetSocketAddress address, int level) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLoggingLevelVerbosityWithNoReply(InetSocketAddress address, int level) throws InterruptedException,
			MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addStateListener(MemcachedClientStateListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeStateListener(MemcachedClientStateListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<MemcachedClientStateListener> getStateListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flushAllWithNoReply(int exptime) throws InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flushAll(int exptime, long timeout) throws TimeoutException, InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flushAllWithNoReply(InetSocketAddress address, int exptime) throws MemcachedException,
			InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flushAll(InetSocketAddress address, long timeout, int exptime) throws MemcachedException,
			InterruptedException, TimeoutException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHealSessionInterval(long healConnectionInterval) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEnableHealSession(boolean enableHealSession) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getHealSessionInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Protocol getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPrimitiveAsString(boolean primitiveAsString) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConnectionPoolSize(int poolSize) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEnableHeartBeat(boolean enableHeartBeat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSanitizeKeys(boolean sanitizeKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSanitizeKeys() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Counter getCounter(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Counter getCounter(String key, long initialValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KeyIterator getKeyIterator(InetSocketAddress address) throws MemcachedException, InterruptedException,
			TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAuthInfoMap(Map<InetSocketAddress, AuthInfo> map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<InetSocketAddress, AuthInfo> getAuthInfoMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long decr(String key, long delta, long initValue, long timeout, int exp) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long incr(String key, long delta, long initValue, long timeout, int exp) throws TimeoutException,
			InterruptedException, MemcachedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Queue<ReconnectRequest> getReconnectRequestQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFailureMode(boolean failureMode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFailureMode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setKeyProvider(KeyProvider keyProvider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTimeoutExceptionThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTimeoutExceptionThreshold(int timeoutExceptionThreshold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invalidateNamespace(String ns) throws MemcachedException, InterruptedException, TimeoutException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invalidateNamespace(String ns, long opTimeout) throws MemcachedException, InterruptedException,
			TimeoutException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endWithNamespace() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginWithNamespace(String ns) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T withNamespace(String ns, MemcachedClientCallable<T> callable) throws MemcachedException,
			InterruptedException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

}
