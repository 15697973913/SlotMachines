
说明：
    自定义各种dialog，其中ToastUtils是个工具类，里面对调用其所在的线程进行了判断，如果是主线程则直接进行UI变化，若在子线程，则调用OutOfThread方法更新UI。