package io.ganguo.library.mvp.http.upload;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import io.ganguo.library.mvp.util.UrlEncoderUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by mikes on 2019/4/5.
 * 示例如何使用ProgressRequestBody
 *
 * fileUploadObserver = object : FileUploadObserver<APIResponseData<UploadFile>>(){
 *             override fun onProgress(progress: Int) {
 *             }
 *
 *             override fun onNext(result: APIResponseData<UploadFile>) {
 *                 super.onNext(result)
 *
 *                 if(result.isSuccessful()){
 *                     completion(result.data?.url)
 *                 } else{
 *                     completion(null)
 *                 }
 *             }
 *
 *             override fun onError(e: Throwable) {
 *                 super.onError(e)
 *
 *                 ToastHelper.toastMessage("上传图片失败，请稍后重试")
 *                 completion(null)
 *             }
 *         }
 *
 *
 * multiPartBody = MultipartBodyHelper.generateFileMultipartBody
 * (
 *      formField = "image",
 *      uploadFile = file,
 *      mimeType = "image/png",
 *      additionalParams = [:],
 *      fileUploadObserver = fileUploadObserver
 * )
 *
 * retrofit service
 *     fun uploadImage(@Body file: MultipartBody): Observable
 *
 * uploadImage(multiPartBody)
 */
public class MultipartBodyHelper {
    /**
     * @param formField          上传文件API接收的文件Field
     * @param uploadFile         待上传文件
     * @param mimeType           文件类型
     * @param additionalParams   额外参数
     * @param fileUploadObserver 上传监听
     * @return Result 文件上传后的数据Bean
     */
    public static <Result> MultipartBody generateFileMultipartBody
    (
            String formField,
            File uploadFile,
            String mimeType,
            Map<String, String> additionalParams,
            FileUploadObserver<Result> fileUploadObserver
    ) {
        MultipartBody.Builder multiPartBodyBuilder = new MultipartBody.Builder();

        ProgressRequestBody uploadFileRequestBody = new ProgressRequestBody<Result>(
                RequestBody.create(MediaType.parse(mimeType), uploadFile),
                fileUploadObserver);

        if (additionalParams != null) {
            //uploadType : uploadType
            for (Map.Entry<String, String> formEntry : additionalParams.entrySet()) {
                multiPartBodyBuilder.addFormDataPart(formEntry.getKey(), formEntry.getValue());
            }
        }
        // 文件名称是中文的时候，需要URLEncoder.encode一次，否则无法上传文件。
        multiPartBodyBuilder.addFormDataPart(formField, UrlEncoderUtils.encoddValue(uploadFile.getName()), uploadFileRequestBody);

        multiPartBodyBuilder.setType(MultipartBody.FORM);
        return multiPartBodyBuilder.build();
    }
}
