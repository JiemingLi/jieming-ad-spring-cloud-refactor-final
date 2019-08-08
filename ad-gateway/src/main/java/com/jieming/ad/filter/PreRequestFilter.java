package com.jieming.ad.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PreRequestFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /*数字越小，优先级越高，因为同类型的过滤器有很多，所以可以通过这个指定哪个过滤去先执行*/
    @Override
    public int filterOrder() {
        return 0;
    }

    /*表示是否执行本过滤器，true表示可以执行，否则相反*/
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /*filter需要执行哪些操作就可以指定*/
    @Override
    public Object run() throws ZuulException {
        /*RequestContext在整个请求的过滤期间，会把这个类型的对象一直传递下去，知道过滤结束，所以可以通过
        * 这个对象进行数据的传递
        * */
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.set("startTime",System.currentTimeMillis());
        return null;
    }
}
