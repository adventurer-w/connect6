package core.board;

/** Describes the classes of Piece on a Connect6 board.
 *  @author Jianliang Xu
 */
public enum PieceColor {
    EMPTY,
    BLACK {
        public PieceColor opposite() {
            return WHITE;
        }

        public boolean isPiece() {
            return true;
        }
    },
    WHITE {
        public PieceColor opposite() {
            return BLACK;
        }

        public boolean isPiece() {
            return true;
        }
    };

    private PieceColor() {
    }

    public PieceColor opposite() {
        throw new UnsupportedOperationException();
    }

    public boolean isPiece() {
        return false;
    }

    public String shortName() {
        return this == BLACK ? "b" : (this == WHITE ? "w" : "-");
    }

    public String toString() {
        return capitalize(super.toString().toLowerCase());
    }

    public static String capitalize(String word) {
        char var10000 = Character.toUpperCase(word.charAt(0));
        return var10000 + word.substring(1);
    }
}
