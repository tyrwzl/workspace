// adder(no carry)
`include "macro"
module adder(a, b, s, op);
  input  [31:0] a, b;
  output [31:0] s;
  input  [31:0] op;
  // input ci;
  // output co;
  // assign {co, s} = a + b + ci;

  case (op):
    `zADD : s = a + b;
    `
endmodule
