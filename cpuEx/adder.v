// adder(no carry)
module adder(a, b, s);
  input  [31:0] a, b;
  output [31:0] s;
  // input ci;
  // output co;

  // assign {co, s} = a + b + ci;
  assign s = a + b;
endmodule
