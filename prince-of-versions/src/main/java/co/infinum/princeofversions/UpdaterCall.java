package co.infinum.princeofversions;

public class UpdaterCall implements PrinceOfVersionsCall {

    private volatile boolean flag;

    @Override
    public void cancel() {
        this.flag = true;
    }

    @Override
    public boolean isCanceled() {
        return flag;
    }
}
