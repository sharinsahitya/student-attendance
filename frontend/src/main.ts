import { Component, inject, OnInit } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { HttpClient, provideHttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

type Status = 'PRESENT' | 'ABSENT' | 'LATE';
interface Row { studentId: number; name: string; rollNumber: string; className: string; status: Status | null; }
@Component({
  selector: 'app-root', standalone: true, imports: [FormsModule],
  template: `
    <header><span class="logo">A</span><div><strong>Attendance</strong><small>STUDENT MANAGEMENT</small></div></header>
    <main>
      <div class="heading"><div><p class="eyebrow">CLASSROOM WORKSPACE</p><h1>Every student counts.</h1><p>Manage your students and keep a daily attendance register.</p></div><label>Attendance date<input type="date" [max]="today" [ngModel]="date" (ngModelChange)="changeDate($event)" [disabled]="busy || dirty"></label></div>
      <div class="stats"><article><span>Total students</span><b>{{rows.length}}</b></article><article><span>Present</span><b>{{count('PRESENT')}}</b></article><article><span>Absent</span><b>{{count('ABSENT')}}</b></article><article><span>Late</span><b>{{count('LATE')}}</b></article></div>
      @if(message) { <p role="status" class="notice">{{message}}</p> }
      <div class="layout"><section><div class="toolbar"><div><h2>Daily register</h2><p>{{dirty ? 'Unsaved changes — save before changing the date.' : 'Choose a status for each student.'}}</p></div><button class="secondary" (click)="markAll()" [disabled]="busy || !rows.length">Mark all present</button></div>
        <div class="table-wrap"><table><thead><tr><th>Student</th><th>Roll number</th><th>Class</th><th>Status</th></tr></thead><tbody>
        @for(row of rows; track row.studentId) { <tr><td>{{row.name}}</td><td>{{row.rollNumber}}</td><td>{{row.className}}</td><td><select [(ngModel)]="row.status" (ngModelChange)="dirty=true" [disabled]="busy" [attr.aria-label]="'Attendance for ' + row.name"><option [ngValue]="null">Unmarked</option><option value="PRESENT">Present</option><option value="ABSENT">Absent</option><option value="LATE">Late</option></select></td></tr> }
        @empty { <tr><td colspan="4">{{busy ? 'Loading students…' : 'No students yet. Add your first student to get started.'}}</td></tr> }
        </tbody></table></div><footer><span>{{rows.length-count('PRESENT')-count('ABSENT')-count('LATE')}} unmarked</span><button (click)="save()" [disabled]="busy || !dirty || !complete">{{busy ? 'Please wait…' : 'Save attendance'}}</button></footer></section>
      <aside><h2>Add a student</h2><p>Build your classroom register.</p><form #studentForm="ngForm" (ngSubmit)="add()"><label>Full name<input name="name" [(ngModel)]="student.name" required maxlength="100"></label><label>Roll number<input name="roll" [(ngModel)]="student.rollNumber" required maxlength="30"></label><label>Class / section<input name="class" [(ngModel)]="student.className" required maxlength="50" placeholder="e.g. Grade 10 — A"></label><button [disabled]="busy || dirty || studentForm.invalid">Add student</button></form></aside></div>
    </main>`
})
class App implements OnInit {
  private http=inject(HttpClient);
  today = new Date(Date.now()-new Date().getTimezoneOffset()*60000).toISOString().slice(0,10);
  date=this.today; rows: Row[]=[]; busy=false; dirty=false; message='';
  student={name:'',rollNumber:'',className:''};
  ngOnInit(){this.load();}
  count(status:Status){return this.rows.filter(r=>r.status===status).length;}
  get complete(){return this.rows.length>0 && this.rows.every(r=>r.status!==null);}
  changeDate(value:string){if(value){this.date=value;this.load();}}
  load(){this.busy=true;this.http.get<Row[]>('/api/attendance',{params:{date:this.date}}).subscribe({next:rows=>{this.rows=rows;this.busy=false;this.dirty=false;},error:e=>this.fail(e)});}
  markAll(){this.rows.forEach(r=>r.status='PRESENT');this.dirty=true;}
  add(){if(!Object.values(this.student).every(s=>s.trim())){this.message='Please fill in every student field.';return;}this.busy=true;this.message='';this.http.post('/api/students',this.student).subscribe({next:()=>{this.student={name:'',rollNumber:'',className:''};this.message='Student added.';this.load();},error:e=>this.fail(e)});}
  save(){this.busy=true;this.message='';this.http.put('/api/attendance',{date:this.date,entries:this.rows.map(r=>({studentId:r.studentId,status:r.status}))}).subscribe({next:()=>{this.busy=false;this.dirty=false;this.message='Attendance saved successfully.';},error:e=>this.fail(e)});}
  fail(e:any){this.busy=false;this.message=e.error?.message || 'Unable to reach the server. Check that the backend is running and try again.';}
}
bootstrapApplication(App,{providers:[provideHttpClient()]}).catch(console.error);
