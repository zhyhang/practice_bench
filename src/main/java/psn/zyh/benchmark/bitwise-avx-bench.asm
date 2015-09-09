;;;;;;;;;;; read me ;;;;;;;;;
;(1) %if 0 ... %endif same as block comment
;(2)	nasm -f elf64 -o bitwise-avx-bench.o bitwise-avx-bench.asm
;	ld -z noexecstack -shared -o bitwise-avx-bench.so bitwise-avx-bench.o

;;;;;;;;;;; macro ;;;;;;;;;;;

%define loop_count 100

; temp hardcode 1000 long i.e. 8000
%define loop_length 16000

; input rdi:JENV*,rax:JENV*->GetPrimitiveArrayCritical*,rdx:jarray
; ouput rax
%macro GetPrimitiveArrayCritical 0
        mov rsi,rdx
        xor rdx,rdx
        call rax
%endmacro

; input rdi:JENV*,rcx:JENV*->ReleasePrimitiveArrayCritical*,rsi:jarray,rax:jarray*
; ouput null
%macro ReleasePrimitiveArrayCritical 0
        mov rdx,rax
	mov rax,rcx
        xor rcx,rcx
	call rax
%endmacro 

;;;;;;;;;;; external function declare ;;;;;;;;;;;

global Java_psn_zyh_benchmark_BitwiseOperationBench_nativeUnion:function

;;;;;;;;;;; section data ;;;;;;;;;;;

section .data

;;;;;;;;;;; section text ;;;;;;;;;;;

section .text

; Java jni interface
;
; In Java class
; 	static native void nativeUnion(long[] al, long[] bl);
;
; In C proto
;	JNIEXPORT jlong JNICALL Java_psn_zyh_benchmark_BitwiseOperationBench_nativeUnion
;  (JNIEnv *, jclass, jlongArray, jlongArray);
;
; void * GetPrimitiveArrayCritical(JNIEnv *env, jarray array, jboolean *isCopy);
; 	index 222
; void ReleasePrimitiveArrayCritical(JNIEnv *env, jarray array, void *carray, jint mode);
; 	index 223
Java_psn_zyh_benchmark_BitwiseOperationBench_nativeUnion:
; call jni interface
	push rbp
	mov rbp,rsp
	sub rsp,56
	mov [rbp-8],rdi
	mov [rbp-16],rdx
	mov [rbp-24],rcx
	mov rsi,[rdi]
	mov rax,[rsi+1776]
	mov [rbp-32],rax
	mov r11,[rsi+1784]
	mov [rbp-40],r11
	GetPrimitiveArrayCritical
	mov [rbp-48],rax ;jarray1*
	mov rdi,[rbp-8]
	mov rdx,[rbp-24] ;jarray2
	mov rax,[rbp-32]
	GetPrimitiveArrayCritical
	mov r11,rax ;jarray2*
	mov rax,[rbp-48]
	xor rcx,rcx
	xor edi,edi
check_outer_loop_end:
	xor esi,esi
do_union:
	vmovdqu ymm0,[rax,rsi]
	vmovdqu ymm1,[r11,rsi]
	vorpd ymm0,ymm1,ymm0
	vmovdqu [rax,rsi],ymm0
	add esi,32
	inc rcx
	cmp esi,loop_length
	jne do_union
	inc edi
	cmp edi,loop_count
	jne check_outer_loop_end
release_ret:
	mov rax,r11
	mov [rbp-56],rcx
	mov rdi,[rbp-8]
	mov rsi,[rbp-24]
	mov rcx,[rbp-40]
	ReleasePrimitiveArrayCritical ;release jarray2*
	mov rdi,[rbp-8]
	mov rsi,[rbp-16]
	mov rcx,[rbp-40]
	mov rax,[rbp-48]
	ReleasePrimitiveArrayCritical ;release jarray1*
	mov rax,[rbp-56]
	leave
	ret

