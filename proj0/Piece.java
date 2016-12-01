public class Piece {
	private boolean isFire = false,
				   	isKing = false,
				   	isBomb = false,
				   	isShield = false,
				   	captured = false;
	private Board b;
	private int p_x, p_y;
	private String type;

	public Piece(boolean isFire, Board b, int x, int y, String type) {
		this.isFire = isFire;
		this.b = b;
		this.p_x = x;
		this.p_y = y;
		this.type = type;

		this.isBomb = type.contains("bomb");
		this.isShield = type.contains("shield");
	}

	public boolean isFire() {
		return isFire;
	}

	public int side() {
		return isFire ? 0 : 1;
	}

	public boolean isKing() {
		return isKing;
	}

	public boolean isBomb() {
		return isBomb;
	}

	public boolean isShield() {
		return isShield;
	}

	public void move(int x, int y) {		
		/** If a piece has reached King Row, king the piece. */
        if ( ! isKing() && ((y == 0 && side() == 1) || (y == 7 && side() == 0))) {
        	type = type.replace(".png", "-crowned.png");
            isKing = true;
        }

        if (Math.abs(p_x - x) == 2 && Math.abs(p_y - y) == 2) {
            Piece target = b.pieceAt(x + (x > p_x ? -1 : 1), y + (y > p_y ? -1 : 1));
            if (target != null && target.side() != side()) {
            	captured = true;
            	b.remove(target.p_x, target.p_y);
		        if (isBomb()) {
		            Piece[] array = {b.pieceAt(x + 1, y + 1), b.pieceAt(x + 1, y - 1), b.pieceAt(x - 1, y - 1), b.pieceAt(x - 1, y + 1)};
		            for (int i = 0; i < 4; i++) {
		                if (array[i] != null) {
		                    if ( ! array[i].isShield()) {
		                    	switch(i) {
		                    		case 0:
		                    			b.remove(x + 1, y + 1);
		                    		break;
		                    		case 1: 
		                    			b.remove(x + 1, y - 1);
		                    		break;
		                    		case 2: 
		                    			b.remove(x - 1, y - 1);
		                    		break;
		                    		case 3:
		                    			b.remove(x - 1, y + 1);
		                    		break;
		                    		default: break;
		                    	}
		                    }
		                }
		            }
            		b.remove(x, y);
		            doneCapturing();
            	}
            }
        }

        p_x = x;
		p_y = y;
	}

	public boolean hasCaptured() {
		return captured;
	}

	public void doneCapturing() {
		captured = false;
	}
}