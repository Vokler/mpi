public class Handle {
    public static void main(String[] args) {
        Blocking.main(args);
        BufferBlocking.main(args);
        NotBlocking.main(args);
        ReadyBlocking.main(args);
        SynchronousBlocking.main(args);
    }
}
