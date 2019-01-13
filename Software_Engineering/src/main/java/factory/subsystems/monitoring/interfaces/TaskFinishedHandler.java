package factory.subsystems.monitoring.interfaces;

public interface TaskFinishedHandler<T> {

	void handleTaskFinished(T task, Object... attachments);
}
