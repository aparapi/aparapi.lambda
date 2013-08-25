" Vim syntax file
" Language:	HSAIL 
" Maintainer:	Gary Frost <frost.gary [at] gmail.com.nl>
" Last Revision:	2013 Aug 25
"
" This is incomplete. Feel free to contribute...
"
"
" http://vim.wikia.com/wiki/Creating_your_own_syntax_files
" http://www.openlogic.com/wazi/bid/188101/Create-Your-Own-Syntax-Highlighting-in-Vim

" For version 6.x: Quit when a syntax file was already loaded
if exists("b:current_syntax")
  finish
endif

syn case ignore

" Partial list of register symbols
syn match hsailReg  "\$[cds][0-9]\+"
syn match hsailReg "%_arg[0-9]+"

" All matches - order is important!
syn match hsailOpcode "mov_[fbusd]64"
syn match hsailOpcode "mov_[fbusd]32"
syn match hsailOpcode "kernarg_[fbusd]32"
syn match hsailOpcode "kernarg_[fbusd]64"
syn match hsailOpcode "ld_kernarg_[fbusd]32"
syn match hsailOpcode "ld_kernarg_[fbusd]64"
syn match hsailOpcode "ld_global_[fbusd]32"
syn match hsailOpcode "ld_global_[fbusd]64"
syn match hsailOpcode "st_global_[fbusd]32"
syn match hsailOpcode "st_global_[fbusd]64"
syn match hsailOpcode "add_[fbusd](32|64)"
syn match hsailOpcode "rem_[fbusd]32"
syn match hsailOpcode "rem_[fbusd]64"
syn match hsailOpcode "sub_[fbusd]32"
syn match hsailOpcode "sub_[fbusd]64"
syn match hsailOpcode "mad_[fbusd]32"
syn match hsailOpcode "mad_[fbusd]64"
syn match hsailOpcode "mul_[fbusd]32"
syn match hsailOpcode "mul_[fbusd]64"
syn match hsailOpcode "div_[fbusd]32"
syn match hsailOpcode "div_[fbusd]64"
syn match hsailOpcode "cvt_[fbusd]64_[fbusd]64"
syn match hsailOpcode "cvt_[fbusd]64_[fbusd]32"
syn match hsailOpcode "cvt_[fbusd]64_[fbusd]16"
syn match hsailOpcode "cvt_[fbusd]64_[fbusd]8"
syn match hsailOpcode "cvt_[fbusd]32_[fbusd]64"
syn match hsailOpcode "cvt_[fbusd]32_[fbusd]32"
syn match hsailOpcode "cvt_[fbusd]32_[fbusd]16"
syn match hsailOpcode "cvt_[fbusd]32_[fbusd]8"
syn match hsailOpcode "cvt_[fbusd]16_[fbusd]64"
syn match hsailOpcode "cvt_[fbusd]16_[fbusd]32"
syn match hsailOpcode "cvt_[fbusd]16_[fbusd]16"
syn match hsailOpcode "cvt_[fbusd]16_[fbusd]8"
syn match hsailOpcode "cvt_[fbusd]8_[fbusd]64"
syn match hsailOpcode "cvt_[fbusd]8_[fbusd]32"
syn match hsailOpcode "cvt_[fbusd]8_[fbusd]16"
syn match hsailOpcode "cvt_[fbusd]8_[fbusd]8"
syn match hsailOpcode "workitemabsid_[busd]32"
syn match hsailOpcode "cbr"
syn match hsailOpcode "cmp_geu_b1_[fbusd]32"
syn match hsailOpcode "cmp_geu_b1_[fbusd]64"
syn match hsailOpcode "cmp_ge_b1_[fbusd]32"
syn match hsailOpcode "cmp_ge_b1_[fbusd]64"
syn match hsailOpcode "cmp_leu_b1_[fbusd]32"
syn match hsailOpcode "cmp_leu_b1_[fbusd]64"
syn match hsailOpcode "cmp_le_b1_[fbusd]32"
syn match hsailOpcode "cmp_le_b1_[fbusd]64"
syn keyword hsailOpcode ret

" Various number formats
syn match hsaildecNumber    "[+-]\=[0-9]\+\>"
syn match hsaildecNumber    "^d[0-9]\+\>"
syn match hsailhexNumber    "^x[0-9a-f]\+\>"
syn match hsailoctNumber    "^o[0-7]\+\>"
syn match hsailbinNumber    "^b[01]\+\>"
syn match hsailfloatNumber  "[-+]\=[0-9]\+E[-+]\=[0-9]\+"
syn match hsailfloatNumber  "[-+]\=[0-9]\+\.[0-9]*\(E[-+]\=[0-9]\+\)\="

" Valid labels
syn match hsailLabel        "@[a-z_$.][a-z0-9_]*:"
syn match hsailLabel        "@[a-z_$.][a-z0-9_]*"

syn match hsailkeyword       "\]"
syn match hsailkeyword       "\["
syn match hsailkeyword       "\$full"
syn match hsailkeyword       "\$large"
syn keyword hsailKeyword      kernel
syn keyword hsailKeyword      version

" Character string constants
"       Too complex really. Could be "<...>" but those could also be
"       expressions. Don't know how to handle chosen delimiters
"       ("^<sep>...<sep>")
" syn region hsailString		start="<" end=">" oneline

" Operators
syn match hsailOperator	"[-+*/!{}()\\]"
syn match hsailOperator	"&[a-z][a-z0-9_]*"


" Special items for comments
syn keyword hsailInline		contained inlined

" Comments
syn match hsailComment		"//.*" contains=hsailInline

syn case match

" Define the default highlighting.
" For version 5.7 and earlier: only when not done already
" For version 5.8 and later: only when an item doesn't have highlighting yet
if version >= 508 || !exists("did_macro_syntax_inits")
  if version < 508
    let did_macro_syntax_inits = 1
    command -nargs=+ HiLink hi link <args>
  else
    command -nargs=+ HiLink hi def link <args>
  endif

  HiLink hsailComment		Comment
  HiLink hsailTodo		Todo

  HiLink hsailhexNumber		Number		" Constant
  HiLink hsailoctNumber		Number		" Constant
  HiLink hsailbinNumber		Number		" Constant
  HiLink hsaildecNumber		Number		" Constant
  HiLink hsailfloatNumber	Number		" Constant
  HiLink hsailReg		Number
  HiLink hsailOperator		Identifier
  HiLink hsailKeyword    	Special
  HiLink hsailOpcode		Statement
  HiLink hsailLabel		Type
  delcommand HiLink
endif

let b:current_syntax = "hsail"

" vim: ts=8 sw=2
