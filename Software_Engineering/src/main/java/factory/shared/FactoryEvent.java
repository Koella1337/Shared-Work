package factory.shared;

import java.util.Arrays;

import factory.shared.enums.EventKind;
import factory.shared.interfaces.Monitorable;

public class FactoryEvent {

	private final Monitorable source;
	private final EventKind kind;
	private final Object[] attachments;
	
	public FactoryEvent(Monitorable source, EventKind kind, Object... attachments) {
		this.source = source;
		this.kind = kind;
		this.attachments = attachments;
		if (!areAttachmentsValid())
			throw new IllegalArgumentException("Invalid Attachments for " + kind.toString());
	}
	
	public Monitorable getSource() {
		return source;
	}

	public EventKind getKind() {
		return kind;
	}
	
	public int getAttachmentCount() {
		return attachments.length;
	}
	
	public Object getAttachment(int attachmentIndex) {
		if (attachmentIndex < 0 || attachmentIndex >= attachments.length)
			throw new IllegalArgumentException("Invalid attachmentIndex!");
		return attachments[attachmentIndex];
	}
	
	public Object[] getAllAttachments() {
		return attachments;
	}
	
	private boolean areAttachmentsValid() {
		Class<?>[] types = kind.attachmentTypes;
		
		if (attachments.length != types.length)
			return false;
		
		for (int i = 0; i < attachments.length; i++) {
			try {
				types[i].cast(attachments[i]);
			} catch (ClassCastException e) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public String toString() {
		return "FactoryEvent [source=" + source + ", kind=" + kind + ", attachments=" + Arrays.toString(attachments)+ "]";
	}
	
}
