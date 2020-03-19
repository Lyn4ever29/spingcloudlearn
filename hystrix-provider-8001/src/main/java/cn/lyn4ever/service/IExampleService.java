package cn.lyn4ever.service;

public interface IExampleService {
    /**
     * 会发生一个错误的方法
     * @param id
     * @return
     */
    String timeOutError(Integer id);

    /**
     * 不发生错误的正确方法
     * @param id
     * @return
     */
    String correct(Integer id);
}
