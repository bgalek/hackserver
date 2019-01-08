package integration.base

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@CompileStatic
class MockMvcHttpClient {

    private final MockMvc mockMvc

    @PackageScope
    MockMvcHttpClient(MockMvc mockMvc) {
        this.mockMvc = Objects.requireNonNull(mockMvc)
    }

    ResultActions get(String path, Map<String, String> queryParams = [:]) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>()
        queryParams.entrySet().each { params.add(it.key, it.value as String) }
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(path)
                        .params(params)
                        .accept(MediaType.APPLICATION_JSON))
        return waitForResponse(resultActions)
    }

    ResultActions post(String path, String body) {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
        return waitForResponse(resultActions)
    }

    private ResultActions waitForResponse(ResultActions resultActions) {
        return isAsync(resultActions) ?
                mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(resultActions.andReturn())) :
                resultActions
    }

    private boolean isAsync(ResultActions resultActions) {
        return resultActions.andReturn().request.isAsyncStarted()
    }
}
