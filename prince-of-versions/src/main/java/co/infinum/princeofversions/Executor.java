package co.infinum.princeofversions;

/**
 * This class provides method for executing update call
 */
public interface Executor {

    /**
     * Executes update call provided through runnable
     *
     * @param runnable Runnable for execution
     */
    void execute(Runnable runnable);

}
