#include <stdio.h>
#include <stdlib.h>

#define swap(type, x, y)    do { type t = x; x = y; y = t; } while (0)

void card_conv(int x, int n, char *d)
{
  char  dchar[] = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  int   i;
  int   digits = 0;                       /* 変換後の桁数 */

  if (x == 0)                             /* 0であれば */
    d[digits++] = dchar[0];             /* 変換後も0 */
  else 
    while (x) {
      d[digits++] = dchar[x % n];     /* nで割った剰余を格納 */
      x /= n;
    }
  for (i = 0; i < digits / 2; i++) {
    swap(char, d[i], d[digits - i - 1]);
    printf("%c", d[i]);
  }
}

void monthEx(char *m, int mo)
{
  if (mo < 10) sprintf(m, "%d", mo);
  else {
    switch (mo) {
      case 10:
        sprintf(m, "%c", 'x');
        break;
      case 11:
        sprintf(m, "%c", 'y');
        break;
      case 12:
        sprintf(m, "%c", 'z');
        break;
    }
  }
}

void conv_file(char *s, int y, int mo, int d, int h, int mi, int se) {
  char st[5];
  char zero[] = "0";
  for (int i = 0; i < 4; ++i) {
    st[i] = zero[0];
  }
  st[4] = '\0';
  int time = h*60*60+mi*60+se;
  printf("%d", time);
  card_conv(time, 36, st);
  char m;
  monthEx(&m, mo);
  sprintf(s, "%d%c%02d%4s.log", y%10, m, d, st);
}

int main() {
  int y = 2015;
  int mo = 7;
  int d = 13;
  int h = 23;
  int mi = 59;
  int se = 59;

  char file[20];
  //file =(char *) malloc(sizeof(char)*20);

  conv_file(file, y, mo, d, h, mi, se);

  printf("%s", file);

}


