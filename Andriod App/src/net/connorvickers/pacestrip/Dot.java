package net.connorvickers.pacestrip;

public class Dot {
	private float location;
	private int direction;
	private float farWall;
	private int positionInQueue;

	public Dot(float farWall, int positionInQueue) {
		this.farWall = farWall;
		this.positionInQueue = positionInQueue;
		direction = 1;
		location = 0f;
	}

	public boolean updateLocation(float speed, long deltaTime, boolean decQueue) {
		boolean ifUpdated = false;// return if queue was decreased
		if (decQueue && positionInQueue > 0) {// decres queue
			positionInQueue--;
			ifUpdated = true;
		}

		if (positionInQueue == 0) {// update location if not in queue
			location = (location + ((deltaTime / 1000f) * speed * direction));
			// check bounds
			if (location > farWall) {// went past far wall
				direction = -1;
				location = (farWall - (location - farWall));
			} else if (location < 0) {// went past near wall
				direction = 1;
				location *= -1;
			}
		}

		return ifUpdated;
	}

	public float getLocation() {
		return location;
	}
}
//TODO uddate db